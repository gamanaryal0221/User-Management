package vcp.np.usermanagement.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import vcp.np.datasource.usermanagement.domains.Client;
import vcp.np.datasource.usermanagement.domains.Service;
import vcp.np.datasource.usermanagement.domains.User;
import vcp.np.datasource.usermanagement.domains.UserClientService;
import vcp.np.datasource.usermanagement.domains.UserInfo;
import vcp.np.datasource.usermanagement.repositories.ClientRepository;
import vcp.np.datasource.usermanagement.repositories.UserClientServiceRepository;
import vcp.np.datasource.usermanagement.repositories.UserInfoRepository;
import vcp.np.datasource.usermanagement.repositories.UserRepository;
import vcp.np.usermanagement.profile.Profile;
import vcp.np.usermanagement.utils.Constants;
import vcp.np.usermanagement.utils.Helper;
import vcp.np.usermanagement.utils.services.AuthenticationService;
import vcp.np.usermanagement.utils.services.PlainSqlQueries;
import vcp.np.usermanagement.utils.services.AuthenticationService.PasswordDetails;

@org.springframework.stereotype.Service
public class UserService {
    
    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final ClientRepository clientRepository;
    private final UserClientServiceRepository userClientServiceRepository;

	private final PlainSqlQueries plainSqlQueries;

    private final AuthenticationService authenticationService;

    private final Profile profile;

    public UserService(UserRepository userRepository, UserInfoRepository userInfoRepository, ClientRepository clientRepository, UserClientServiceRepository userClientServiceRepository, PlainSqlQueries plainSqlQueries, AuthenticationService authenticationService, Profile profile) {
        this.userRepository = userRepository;
        this.userInfoRepository = userInfoRepository;
        this.clientRepository = clientRepository;
        this.userClientServiceRepository = userClientServiceRepository;

        this.plainSqlQueries = plainSqlQueries;

        this.authenticationService = authenticationService;
        this.profile = profile;
    }


    public Specification<User> getListOfUsersSpecification(Map<String, String[]> params) {

        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {

            Predicate predicate = criteriaBuilder.conjunction();

            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("softDeleted"), false));

            String id = params.get("id")[0];
            if (id != null && !id.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("id"), id));
            }

            String username = params.get("username")[0];
            if (username != null && !username.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("username"), "%" + username + "%"));
            }

            String mailAddress = params.get("mailAddress")[0];
            if (mailAddress != null && !mailAddress.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("mailAddress"), "%" + mailAddress + "%"));
            }

            String fullname = params.get("fullname")[0];
            if (fullname != null && !fullname.isEmpty()) {

                String[] splittedName = fullname.split(" ");
                int length = splittedName.length;
                if (length == 1){
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("firstName"), "%" + splittedName[0] + "%"));
                }else if (length == 2) {
                    
                    predicate = criteriaBuilder.or(
                            criteriaBuilder.and(
                                    criteriaBuilder.equal(root.get("firstName"), splittedName[0]),
                                    criteriaBuilder.equal(root.get("middleName"), splittedName[1])
                            ),
                            criteriaBuilder.and(
                                    criteriaBuilder.equal(root.get("firstName"), splittedName[0]),
                                    criteriaBuilder.equal(root.get("lastName"), splittedName[1])
                            )
                    );

                }else {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("firstName"), "%" + splittedName[0] + "%"));
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("middleName"), "%" + splittedName[1] + "%"));
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("lastName"), "%" + splittedName[2] + "%"));
                }
            }
            
            return predicate;
        };
    }

    public Map<String, Object> getUserList(HttpServletRequest request) {
        Map<String,String[]> paramsMap = request.getParameterMap();

        Specification<User> specification = getListOfUsersSpecification(paramsMap);
        Pageable pageable = Helper.getPageable(paramsMap, 20);

        Page<User> pageUsers = userRepository.findAll(specification, pageable);

        List<Map<String, Object>> userList = new ArrayList<>();
        for(User user : pageUsers.getContent()) {
            Map<String, Object> userMap = new HashMap<>();

            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("mailAddress", user.getMailAddress());
            userMap.put("fullname", user.getFullname());

            List<Map<String, Object>> clientServiceMapList = plainSqlQueries.getAllClientAndServiceOfUser(user.getId());
            userMap.put("clientDisplayNameList", clientServiceMapList.stream().map(row -> (String) row.get("clientDisplayName")).distinct().sorted().collect(Collectors.joining(", ")));
            userMap.put("serviceDisplayNameList", clientServiceMapList.stream().map(row -> (String) row.get("serviceDisplayName")).distinct().sorted().collect(Collectors.joining(", ")));

            userList.add(userMap);
        }

        Map<String, Object> response = Helper.getTableMetadata(pageUsers);
        response.put(Constants.CONTENT, userList);

        return response;
    }

    public boolean isUsernameExists(String username) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(username);
            return (userOptional != null && userOptional.isPresent() && userOptional.get() != null);
        } catch (Exception ex) {
            System.out.println("Error encountered in isUsernameExists(username:" + username + ")");
            ex.printStackTrace();
            return true;
        }
    }

    public Map<String, Object> saveUser(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {

            String firstName = request.getParameter("firstName");
            if (firstName == null || firstName.isEmpty()) {
                response.put(Constants.Error.Message.KEY, Constants.Error.Message.FIRSTNAME_IS_MANDATORY);
                return response;
            }

            String lastName = request.getParameter("lastName");
            if (lastName == null || lastName.isEmpty()) {
                response.put(Constants.Error.Message.KEY, Constants.Error.Message.LASTNAME_IS_MANDATORY);
                return response;
            }

            String mailAddress = request.getParameter("mailAddress");
            if (mailAddress == null || mailAddress.isEmpty()) {
                response.put(Constants.Error.Message.KEY, Constants.Error.Message.USERNAME_IS_MANDATORY);
                return response;
            }
            if (!Helper.isValidMailAddress(mailAddress)) {
                response.put(Constants.Error.Message.KEY, Constants.Error.Message.INVALID_MAIL);
                return response;
            }

            String username = request.getParameter("username");
            if (username == null || username.isEmpty()) {
                response.put(Constants.Error.Message.KEY, Constants.Error.Message.USERNAME_IS_MANDATORY);
                return response;
            }
            if (isUsernameExists(username)) {
                response.put(Constants.Error.Message.KEY, Constants.Error.Message.USERNAME_TAKEN);
                return response;
            }
            
            Optional<Client> optionalEmployerClient = null;
            Optional<Client> optionalSessionClient = null;
            boolean isAdminClient = (boolean) request.getSession().getServletContext().getAttribute(Constants.Session.IS_ADMIN_CLIENT);
            System.out.println("isAdminClient: " + isAdminClient);
            if (isAdminClient){
                System.out.println("admin in");
                String employerId = request.getParameter("employerId");
                if (employerId == null || employerId.isEmpty()) {
                    response.put(Constants.Error.Message.KEY, Constants.Error.Message.EMPLOYER_IS_MANDATORY);
                    return response;
                }

                optionalEmployerClient = clientRepository.findById(Long.parseLong(employerId));
                optionalSessionClient = clientRepository.findById(Long.parseLong(profile.getProperty("adminClient.id")));
            } else {
                System.out.println("non admin in");

                optionalEmployerClient = clientRepository.findById(Long.parseLong((String) request.getSession().getAttribute(Constants.Session.CLIENT_ID)));
                optionalSessionClient = optionalEmployerClient;
            }

            if (optionalEmployerClient == null || optionalEmployerClient.isEmpty() || optionalEmployerClient.get() == null) {
                response.put(Constants.Error.Message.KEY, Constants.Error.Message.EMPLOYER_NOT_FOUND);
                return response;
            }
            
            User user = new User(username, firstName, request.getParameter("middleName"), lastName, mailAddress);
            user.setEmployer(optionalEmployerClient.get());

            String autoGeneratedPassword = Helper.generateRandomValue(12);
            PasswordDetails passwordDetails = authenticationService.makePassword(autoGeneratedPassword);
            user.setSaltValue(passwordDetails.getSaltValue());
            user.setPassword(passwordDetails.getHashedPassword());

            user = userRepository.save(user);

            try {

                boolean isUserInfoSaved = saveUserInfo(user, (String) request.getSession().getServletContext().getAttribute(Constants.Session.USER_ID), optionalSessionClient.get());
                if (isUserInfoSaved) {
                    response.put("userId", user.getId());
                    return response;
                } else {
                    throw new Exception("Could not save userinfo of user[username:" + username +", mailAddress:" + mailAddress + "]");
                }
            } catch (Exception e) {
                e.printStackTrace();
                userRepository.delete(user);

                throw new Exception("Error encountered while saving userinfo of user[username:" + username +", mailAddress:" + mailAddress + "]");
            }

        }catch(Exception e) {
            e.printStackTrace();
            
            response.put(Constants.Error.Message.KEY, Constants.Error.Message.SMTH_WENT_WRONG);
            return response;
        }

    }

    private boolean saveUserInfo(User user, String sessionUserId, Client sessionClient) throws Exception {

        Optional<User> optionalSessionUser = userRepository.findById(Long.parseLong(sessionUserId));
        if (optionalSessionUser == null || !optionalSessionUser.isPresent() || optionalSessionUser.get() == null) {
            throw new Exception("Could not find session user[id:" + sessionUserId + "]");
        }

        UserInfo userInfo = new UserInfo(user, optionalSessionUser.get(), optionalSessionUser.get(), sessionClient);
        userInfoRepository.save(userInfo);
        return (userInfo.getId() != null)? true:false;
    }


    public Map<String, Object> getUserProfile(Long userId) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional == null || !userOptional.isPresent() || userOptional.get() == null) {
            return Helper.error(Constants.Error.Title.NOT_FOUND, Constants.Error.Message.USER_NOT_FOUND);
        }

        User user = userOptional.get();
        response.put("user", userOptional.get());
        response.put("userFullName", user.getFullname());
        response.put("userCreatedAt", user.getCreatedAt().toLocaleString());
        response.put("userLastUpdatedAt", user.getCreatedAt().toLocaleString());

        Optional<UserInfo> userInfoOptional = userInfoRepository.findByUserId(userId);
        if (userInfoOptional == null || !userInfoOptional.isPresent() || userInfoOptional.get() == null) {
            response.put("createdBy", null);
            response.put("lastUpdatedBy", null);
            response.put("createdFromClient", null);
        }else {
            UserInfo userInfo = userInfoOptional.get();

            Map<String, Object> userCreatedByMap = new HashMap<>();
            userCreatedByMap.put("id", userInfo.getCreatedBy().getId());
            userCreatedByMap.put("fullname", userInfo.getCreatedBy().getFullname());
            response.put("userCreatedBy", userCreatedByMap);
            
            Map<String, Object> userlastUpdatedByMap = new HashMap<>();
            userlastUpdatedByMap.put("id", userInfo.getLastUpdatedBy().getId());
            userlastUpdatedByMap.put("fullname", userInfo.getLastUpdatedBy().getFullname());
            response.put("userLastUpdatedBy", userlastUpdatedByMap);
            
            response.put("createdFromClient", userInfo.getCreatedFromClient());
        }

        System.out.println("userCreatedBy : " + response.get("userCreatedBy"));
        System.out.println("userLastUpdatedBy : " + response.get("userLastUpdatedBy"));


        return response;
    }


    
    public Specification<UserClientService> getListOfUserAccessSpecification(User user, Map<String, String[]> params) {

        return (Root<UserClientService> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {

            Predicate predicate = criteriaBuilder.conjunction();

            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("user"), user));

            List<Long> clientServiceIdList = plainSqlQueries.getClientServiceIdListByClientAndServiceName(params.get("client")[0], params.get("service")[0]);
            if (clientServiceIdList != null) {
                predicate = criteriaBuilder.and(predicate, root.get("clientService").get("id").in(clientServiceIdList));
            }

            return predicate;
        };
    }

    public Object getUserAccessList(Long userId, HttpServletRequest request) throws Exception{
        Map<String,String[]> paramsMap = request.getParameterMap();

        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional == null || !userOptional.isPresent() || userOptional.get() == null) {
            throw new Exception("Didnot find user on DB: user[id:" + userId + "]");
        }

        User user = userOptional.get();
        Specification<UserClientService> specification = getListOfUserAccessSpecification(user, paramsMap);
        Pageable pageable = Helper.getPageable(paramsMap, 10);

        Page<UserClientService> pageUsers = userClientServiceRepository.findAll(specification, pageable);

        List<Map<String, Object>> userAccessList = new ArrayList<>();
        for(UserClientService userClientService : pageUsers.getContent()) {
            Map<String, Object> userAccessMap = new HashMap<>();

            userAccessMap.put("accessProvidedDate", userClientService.getCreatedAt().toLocaleString());

            Client client = userClientService.getClient();
            userAccessMap.put("clientId", client.getId());
            userAccessMap.put("clientDisplayName", client.getDisplayName());

            Service service = userClientService.getService();
            userAccessMap.put("serviceId", service.getId());
            userAccessMap.put("serviceDisplayName", service.getDisplayName());

            userAccessMap.put("lastLoggedInDate", (userClientService.getLastLoggedInAt() == null)? "Never Logged in":userClientService.getLastLoggedInAt().toLocaleString());

            userAccessList.add(userAccessMap);
        }

        Map<String, Object> response = Helper.getTableMetadata(pageUsers);
        response.put(Constants.CONTENT, userAccessList);

        return response;
    }
}

package vcp.np.usermanagement.utils.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;


@Repository
public class PlainSqlQueries {

	@PersistenceContext
    private EntityManager entityManager;
    
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllClientAndServiceOfUser(Long userId) {
    	
    	if (userId == null ) {
            System.out.println("user:" + userId + " is null in getAllClientAndServiceOfUser");
            return null;
    	}
    	
    	try {
    		String queryStr = ""
            		+ "select distinct "
                    + " c.id, c.name, c.display_name, "
                    + " s.id, s.name, s.display_name "
            		+ "from user_client_service ucs "
            		+ "	inner join client_service cs on cs.id = ucs.client_service_id "
            		+ "	inner join client c on c.id = cs.client_id "
            		+ "	inner join service s on s.id = cs.service_id "
            		+ "where ucs.user_id = :userId ";
            
            Query query = entityManager.createNativeQuery(queryStr);
            query.setParameter("userId", userId);
            
            List<Map<String, Object>> resultList = new ArrayList<>();
            List<?> rows = query.getResultList();

            for (Object row : rows) {

                Object[] columns = (Object[]) row;
                Map<String, Object> rowMap = new HashMap<>();

                rowMap.put("clientId", columns[0]);
                rowMap.put("clientName", columns[1]);
                rowMap.put("clientDisplayName", columns[2]);
                rowMap.put("serviceId", columns[3]);
                rowMap.put("serviceName", columns[4]);
                rowMap.put("serviceDisplayName", columns[5]);

                resultList.add(rowMap);
            }

            // if(resultList.size() > 0) System.out.println(userId + " -> " + resultList);
            return resultList;
            
        } catch (Exception ex) {
            System.out.println("Error encountered in getAllClientAndServiceOfUser(userId:" + userId + ")");
            ex.printStackTrace();
            return null;
        }
    }

    
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getServiceListByClientIdList(Long userId) {
    	
    	if (userId == null ) {
            System.out.println("user:" + userId + " is null in getAllClientAndServiceOfUser");
            return null;
    	}
    	
    	try {
    		String queryStr = ""
            		+ "select distinct "
                    + " c.id, c.name, c.display_name, "
                    + " s.id, s.name, s.display_name "
            		+ "from user_client_service ucs "
            		+ "	inner join client_service cs on cs.id = ucs.client_service_id "
            		+ "	inner join client c on c.id = cs.client_id "
            		+ "	inner join service s on s.id = cs.service_id "
            		+ "where ucs.user_id = :userId ";
            
            Query query = entityManager.createNativeQuery(queryStr);
            query.setParameter("userId", userId);
            
            List<Map<String, Object>> resultList = new ArrayList<>();
            List<?> rows = query.getResultList();

            for (Object row : rows) {

                Object[] columns = (Object[]) row;
                Map<String, Object> rowMap = new HashMap<>();

                rowMap.put("clientId", columns[0]);
                rowMap.put("clientName", columns[1]);
                rowMap.put("clientDisplayName", columns[2]);
                rowMap.put("serviceId", columns[3]);
                rowMap.put("serviceName", columns[4]);
                rowMap.put("serviceDisplayName", columns[5]);

                resultList.add(rowMap);
            }

            if(resultList.size() > 0) System.out.println(userId + " -> " + resultList);
            return resultList;
            
        } catch (Exception ex) {
        	System.out.println("Error encountered in getAllClientAndServiceOfUser(userId:" + userId + ")");
            ex.printStackTrace();
            return null;
        }
    }

    
    @Transactional(readOnly = true)
    public boolean isItInUserPasswordHistory(Long userId, String rawPassword) {
    	
    	if (userId == null || rawPassword == null || rawPassword.isEmpty()) {
            System.out.println("userId:" + userId + " or rawPassword:" + rawPassword + "is null or empty in isItInUserPasswordHistory");
            return false;
    	}
    	
    	try {
            String queryStr = ""
            		+ "select count(*) "
            		+ "from user_password_history "
            		+ "where user_id = :userId "
            		+ "	and password = sha2(concat(salt_value, :rawPassword), 512) ";
            
            Query query = entityManager.createNativeQuery(queryStr);
            query.setParameter("userId", userId);
            query.setParameter("rawPassword", rawPassword);
            
            Object result = query.getSingleResult();
            if (result instanceof Number) {
            	
            	boolean isInUserPasswordHistory = (((Number) result).intValue() == 1);
            	System.out.println("Is it in user[id: " + userId + "]'s password history?\n>> " + isInUserPasswordHistory);
            	
                return isInUserPasswordHistory;
            	
            } else {
                throw new IllegalStateException("Query result is not a number");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        	System.out.println("Error encountered while checking user[id: " + userId + "]'s password history");
            return false;
        }
    }


    public List<Long> getClientServiceIdListByClientAndServiceName(String clientName, String serviceName) {
    	
    	try {
    		String queryStr = ""
            		+ "select distinct cs.id "
            		+ "from client_service cs "
            		+ "	inner join client c on c.id = cs.client_id "
            		+ "	inner join service s on s.id = cs.service_id ";

            
            Map<String, String> params = new HashMap<>();
            if (clientName != null && !clientName.isEmpty() && serviceName != null && !serviceName.isEmpty()) {
                queryStr = queryStr 
            		+ "where (c.name like :clientName or c.display_name like :clientName) "
                    + " and (s.name like :serviceName or s.display_name like :serviceName) ";
                
                params.put("clientName", "%" + clientName + "%");
                params.put("serviceName", "%" + serviceName + "%");

            } else if (clientName != null && !clientName.isEmpty()) {
                queryStr = queryStr + "where c.name like :clientName or c.display_name like :clientName ";
                params.put("clientName", "%" + clientName + "%");

            } else if (serviceName != null && !serviceName.isEmpty()) {
                queryStr = queryStr + "where s.name like :serviceName or s.display_name like :serviceName ";
                params.put("serviceName", "%" + serviceName + "%");
                
            } else {
                System.out.println("clientName:" + clientName + ", and serviceName:" + serviceName + " is null or empty in getClientServiceIdListByClientAndServiceName");
                return null;
            }
            
            Query query = entityManager.createNativeQuery(queryStr);
            for (String paramKey : params.keySet()) {
                query.setParameter(paramKey, params.get(paramKey));
            }
            
            List<Long> clientServiceIdList = new ArrayList<>();
            for (Object row : query.getResultList()) {
                clientServiceIdList.add(Long.valueOf(row.toString()));
            }

            return clientServiceIdList;
            
        } catch (Exception ex) {
        	System.out.println("Error encountered in getClientServiceIdListByClientAndServiceName(clientName:" + clientName + ", serviceName:" + serviceName + ")");
            ex.printStackTrace();
            return null;
        }
    }
    
}

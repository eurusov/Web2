package service;

import model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class UserService {

    private static UserService instance;

    /* хранилище данных */
    private Map<Long, User> dataBase = Collections.synchronizedMap(new HashMap<>());
    /* счетчик id */
    private AtomicLong maxId = new AtomicLong(0);
    /* список авторизованных пользователей */
    private Map<Long, User> authMap = Collections.synchronizedMap(new HashMap<>());

    private UserService() {
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public List<User> getAllUsers() {
        if (dataBase.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(dataBase.values());
//        return Collections.list((Enumeration<User>) dataBase.values());
    }

    public User getUserById(Long id) {
        return dataBase.get(id);
    }

    public boolean addUser(User user) {
        if (isExistsThisUser(user)) {
            return false;
        }

        user.setId(maxId.longValue());
        try {
            dataBase.put(maxId.getAndIncrement(), user);
        } catch (RuntimeException e) {
            maxId.decrementAndGet();
            return false;
        }
        return true;
    }

    public void deleteAllUser() {
        dataBase.clear();
    }

    /**
     * Проверяет есть ли user с таким email в dataBase.
     * если есть - меняет у user поле id на id из dataBase и возвращает true.
     * Иначе - записывает в поле user.id -1 и возвращает false
     */
    public boolean isExistsThisUser(User user) {
        List<User> users = getAllUsers();
        long[] id = {-1};
        users.forEach(u -> {
            if (u.getEmail().equals(user.getEmail())) {
                id[0] = u.getId();
            }
        });
        user.setId(id[0]);
        return id[0] != -1;
    }

    public List<User> getAllAuth() {
        // (!) Уточнить, возможно if не нужен
        if (authMap.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(authMap.values());
    }

    public boolean authUser(User user) {
        if (!isExistsThisUser(user)) { // Если user с таким email есть в dataBase, то теперь его поле id соответствует id из базы
            return false;
        }
        if (isUserAuthById(user.getId())) {
            return true;
        }
        authMap.put(user.getId(), user);
        return true;
    }

    public void logoutAllUsers() {
        authMap.clear();
    }

    public boolean isUserAuthById(Long id) {
        return authMap.containsKey(id);
    }
}

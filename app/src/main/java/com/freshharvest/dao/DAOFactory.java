package com.freshharvest.dao;

public class DAOFactory {
    private static FactoryDAO factoryDAOInstance;
    
    public static FactoryDAO getFactoryDAO() {
        if (factoryDAOInstance == null) {
            factoryDAOInstance = new FactoryDAOImpl();
        }
        return factoryDAOInstance;
    }
    
    public static void reset() {
        factoryDAOInstance = null;
    }
}
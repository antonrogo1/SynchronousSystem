package com.syncsys.factories;

/**
 * Created by z on 3/26/17.
 * The idea behind the FactoryHolder is to make it easier to switch modes by simply changing the factory implementation.
 */
public class FactoryHolder {
    private static Factory factory;
    public static void setFactory(Factory implementation){
        factory = implementation;
    }

    public static Factory getFactory(){
        return factory;
    }

}

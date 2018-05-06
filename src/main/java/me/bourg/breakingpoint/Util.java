package me.bourg.breakingpoint;

public class Util {
    public static String externalizeClassName(String internalName) {
        return internalName.replace('/', '.');
    }
}

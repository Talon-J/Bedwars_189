package me.camm.productions.bedwars.Util;

public enum Sites
{
    UUID_CONVERT("https://api.mojang.com/users/profiles/minecraft/"),
    PROFILE_GET("https://sessionserver.mojang.com/session/minecraft/profile/"),
    PROFILE_CAPPER("?unsigned=false");



    private final String url;

    private Sites(String url)
    {
        this.url = url;
    }

    public String getURL()
    {
        return this.url;
    }
}

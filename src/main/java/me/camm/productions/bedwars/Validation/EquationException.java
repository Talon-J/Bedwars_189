package me.camm.productions.bedwars.Validation;

import org.bukkit.ChatColor;


//when we try to equate something to a value but are unable to
public class EquationException extends ParameterException
{
   private final String result;
    public EquationException(String file, int line, String expectedResult, String given, String result) {
        super(file, line, expectedResult, given);
        this.result = result;
    }

    public String toString(){
        return getMessage();
    }

    @Override
    public String getMessage() {
        return ChatColor.RED+"Could not resolve value: File:"+file+" Line: "+line+" Expected result:"+expected+" Result: "+result;
    }
}

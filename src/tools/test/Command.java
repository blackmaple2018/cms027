/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.test;

import client.player.SpeedQuiz;
import database.Database;

/**
 *
 * @author Administrator
 */
public class Command {
    public static void main(String[] args) {
        try {
            Database.createDatabase();
        } catch (NoClassDefFoundError ndfe) {
            ndfe.printStackTrace();
        }
    }
}

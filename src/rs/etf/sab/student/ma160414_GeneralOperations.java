/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import rs.etf.sab.operations.GeneralOperations;

/**
 *
 * @author acamr
 */
public class ma160414_GeneralOperations implements GeneralOperations {

    public ma160414_GeneralOperations() {
    }

    @Override
    public void eraseAll() {
        DB.call("spEraseAll");
    }
    
}

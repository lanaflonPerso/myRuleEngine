package com.idomine.business;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;

import com.idomine.model.Customer;
import com.idomine.model.Sale;
import com.idomine.myruleengine.MyRuleEngine;

@Named
@ViewScoped
public class BusinessBean implements Serializable
{
    private static final long serialVersionUID = 1L;

    public void fireRules()
    {
        // facts
        Sale sale = Sale.getFake();
        Customer customer = Customer.getFake();
        sale.setCustomer(customer);

        // rules
        SaleRule saleRule = new SaleRule();
        PersonRule customerRule = new PersonRule();

        // RuleEngine 1
        MyRuleEngine re1 = new MyRuleEngine();
        re1.setMensagemCheckTrue("Success!");
        re1.setMensagemCheckFalse("Validations fails!");
        re1.setMensagemChecking("Checking rules...");
        re1.setClassOutputMesagem(NotificationsHelper.class);

        // RuleEngine 2
        MyRuleEngine re2 = MyRuleEngine
                .Builder()
                .addFact("customer", customer)
                .addFact("sale", sale)
                .addFact("minimal", new BigDecimal(11))

                .addClassRule(saleRule)
                .addAllMethods()

                .addNewClassRule(customerRule)
                .addAllMethods()

                .buildRules();

        // RuleEngine 1 + 2
        re1.addRuleEngine(re2);

        // fire rules
        re1.fireRules();
    }

}
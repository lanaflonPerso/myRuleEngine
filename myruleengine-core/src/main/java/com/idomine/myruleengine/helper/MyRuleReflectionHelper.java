/**
 * The MIT License
 *
 *  Copyright (c) 2017, Lyndon Tavares (integraldominio@gmail.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.idomine.myruleengine.helper;

import static com.idomine.myruleengine.exceptions.ExceptionHelper.myRuleReturnTypeException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.idomine.myruleengine.MyRuleFact;
import com.idomine.myruleengine.MyRuleMethod;
import com.idomine.myruleengine.annotations.InjectFact;
import com.idomine.myruleengine.annotations.RuleCondition;
import com.idomine.myruleengine.exceptions.ExceptionHelper;
import com.idomine.myruleengine.exceptions.MyRuleConvertionException;
import com.idomine.myruleengine.notification.Notification;

public final class MyRuleReflectionHelper
{
    private MyRuleReflectionHelper()
    {
    }

    public static Object execute(Object o, String metodo, Object... objects)
    {
        Object retorno = false;
        for (Method m : o.getClass().getMethods())
        {
            if (m.getName().equals(metodo))
            {

                checarTipoRetornoMetodo(m);

                try
                {
                    retorno = m.invoke(o);
                    break;
                }
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return retorno;
    }

    private static void checarTipoRetornoMetodo(Method m)
    {
        if (!m.getReturnType().equals(Notification.class))
        {
            myRuleReturnTypeException(m.getName());
        }
    }

    
    public static void executeNotificacao(Object o, String msg, Class<?> anotacao)
    {
        if ( o.getClass().equals( Class.class) )
        {
            executeNotify( (Class<?>) o , msg, anotacao);
        }
        else
        {
            executeNotify(  o , msg, anotacao);

        }
    }
    
    private static void executeNotify(Class<?> o, String msg, Class<?> anotacao)
    {
        Class<?> cc = null;
        try
        {
            cc = Class.forName(o.getName());
        }
        catch (ClassNotFoundException e1)
        {
            e1.printStackTrace();
        }

        for (Method m : cc.getMethods())
        {
            if (metodoContemAnotacao(m, anotacao))
            {
                try
                {
                    m.invoke(o, msg);
                    break;
                }
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private static void executeNotify(Object o, String msg, Class<?> anotacao)
    {
        for (Method m : o.getClass().getMethods())
        {
            if (metodoContemAnotacao(m, anotacao))
            {
                try
                {
                    m.invoke(o, msg);
                    break;
                }
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void prepareFacts(Object o, String metodo, Object... objects)
    {
        for (Method m : o.getClass().getMethods())
        {
            if (m.getName().equals(metodo))
            {
                try
                {
                    m.invoke(o);
                    break;
                }
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void prepareFacts(Object classe, List<MyRuleFact> fatos) throws MyRuleConvertionException
    {
        if (fatos != null)
            for (MyRuleFact fato : fatos)
            {
                atribuirFato(classe, fato);
            }
    }

    private static void atribuirFato(Object classe, MyRuleFact fato) throws MyRuleConvertionException
    {
        Field field = localizarAtributo(classe, fato.getNome());
        if (field != null)
        {
            try
            {
                field.setAccessible(true);
                field.set(classe, fato.getValor());
                
            }
            catch (IllegalArgumentException | IllegalAccessException e)
            {
                
                ExceptionHelper.myRuleCovertionException("ClassRule:["+classe.getClass().getName()+"] fact:["+fato.getNome()+"] "+e.getMessage());
                
            }
            
            
        }
    }

    private static Field localizarAtributo(Object classe, String nome)
    {
        // 1st by atribute name
        for (Field field : classe.getClass().getDeclaredFields())
        {
            if (contemAnotacao(field, nome))
            {
                return field;
            }
        }
        return null;
    }

    private static boolean metodoContemAnotacao(Method method, Class<?> anotacao)
    {
        Annotation[] annotations = method.getDeclaredAnnotations();
        if (annotations.length != 0)
            for (int j = 0; j < annotations.length; j++)
            {
                if ((annotations[j].annotationType() == anotacao))
                {
                    return true;
                }
            }
        return false;
    }

    private static boolean contemAnotacao(Field field, String nome)
    {
        Annotation[] annotations = field.getDeclaredAnnotations();
        if (annotations.length != 0)
            for (int j = 0; j < annotations.length; j++)
            {
                String[] aName = ((InjectFact) annotations[j]).name();

                if ((annotations[j].annotationType() == InjectFact.class)
                        && aName!=null && aName.length > 0
                        && nome.equals(aName[0]))
                {
                    return true;
                }
                else if ((annotations[j].annotationType() == InjectFact.class)
                        && aName!=null && aName.length == 0 && field.getName().equals(nome) )
                {
                    return true;
                }
                
                
            }
        return false;
    }

    public static List<String> metodosNotificaveisReturnandoNotificacao(Object rule)
    {
        List<String> metodos = new ArrayList<>();
        for (Method metodo : metodosPublicos(rule))
        {
            if (metodo.getReturnType().equals(Notification.class))
            {
                metodos.add(metodo.getName());
            }
        }
        return metodos;
    }

    public static List<String> metodosNotificaveisRetornandoListNotificacao(Object rule)
    {
        List<String> metodos = new ArrayList<>();
        for (Method metodo : metodosPublicos(rule))
        {
            if (metodo.getReturnType().equals(List.class))
            {
                metodos.add(metodo.getName());
            }
        }
        return metodos;
    }

    public static List<String> metodosNotificaveisRetornandoBoolean(Object rule)
    {
        List<String> metodos = new ArrayList<>();
        for (Method metodo : metodosPublicos(rule))
        {
            if (metodo.getReturnType().equals(Boolean.class))
            {
                metodos.add(metodo.getName());
            }
        }
        return metodos;
    }

    public static List<Method> metodosPublicos(Object rule)
    {
        List<Method> listaRetorno = new ArrayList<>();

        for (Method m : rule.getClass().getMethods())
        {
            try
            {
                listaRetorno.add(m);
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            }
        }
        return listaRetorno;
    }

    public static boolean metodoNotificavel(Object rule, String nomeMetodo)
    {
        return metodosNotificaveisPorNome(rule).indexOf((Object) nomeMetodo) > -1;
    }

    public static List<String> metodosNotificaveisPorNome(Object rule)
    {
        List<String> listaRetorno = new ArrayList<>();

        for (Method m : rule.getClass().getMethods())
        {
            if (m.getReturnType().equals(Notification.class))
            {
                try
                {
                    listaRetorno.add(m.getName());
                }
                catch (IllegalArgumentException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return listaRetorno;
    }

    public static List<MyRuleMethod> metodosNotificaveis(Object rule)
    {
        List<MyRuleMethod> listaRetorno = new ArrayList<>();

        for (Method m : rule.getClass().getMethods())
        {
            if (m.getReturnType().equals(Notification.class))
            {
                try
                {
                    listaRetorno.add(new MyRuleMethod(m.getName()));
                }
                catch (IllegalArgumentException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return listaRetorno;
    }

    public static List<MyRuleMethod> ordenarPorPrioridade(Object rule, List<MyRuleMethod> metodos)
    {
        for (MyRuleMethod metodo : metodos)
        {
            metodo.setPrioridade(prioridade(rule, metodo));

        }

        metodos = metodos.stream().sorted((p1, p2) -> p1.getPrioridade().compareTo(p2.getPrioridade()))
                .collect(Collectors.toList());

        return metodos;

    }

    private static long prioridade(Object rule, MyRuleMethod metodo)
    {
        for (Method m : rule.getClass().getMethods())
        {
            if (m.getName().equals(metodo.getNome()))
            {
                try
                {
                    return getPrioridadeMetodo(m, metodo.getNome());
                }
                catch (IllegalArgumentException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return 999999;
    }

    private static long getPrioridadeMetodo(Method metodo, String nome)
    {
        Annotation[] annotations = metodo.getDeclaredAnnotations();
        if (annotations.length != 0)
            for (int j = 0; j < annotations.length; j++)
            {
                if (annotations[j].annotationType() == RuleCondition.class)
                {
                    return ((RuleCondition) annotations[j]).prioridade();
                }
            }
        return 999999;
    }

}

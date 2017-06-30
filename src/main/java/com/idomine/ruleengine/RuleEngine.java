package com.idomine.ruleengine;

import static com.idomine.ruleengine.helper.RuleEngineHelper.*;
import static com.idomine.ruleengine.exceptions.ExceptionHelper.checkNull;
import static com.idomine.ruleengine.exceptions.ExceptionHelper.myRuleException;
import static com.idomine.ruleengine.exceptions.ExceptionHelper.*;
import static com.idomine.ruleengine.helper.JavaClassHelper.isJavaMethodName;

import java.util.ArrayList;
import java.util.List;

import com.idomine.ruleengine.annotations.NotificacaoContext;
import com.idomine.ruleengine.annotations.NotificacaoError;
import com.idomine.ruleengine.annotations.NotificacaoInfo;
import com.idomine.ruleengine.annotations.NotificacaoWarn;
import com.idomine.ruleengine.helper.RuleEngineHelper;
import com.idomine.ruleengine.notification.Mensagem;
import com.idomine.ruleengine.notification.MensagemTipo;
import com.idomine.ruleengine.notification.Notificacao;

public class RuleEngine
{
    private boolean result;
    private boolean notifications = false;
    private String mensagemChecking;
    private String mensagemCheckTrue;
    private String mensagemCheckFalse;
    private List<RuleFact> fatos;
    private List<RuleModel> ruleModel;
    private Class<?> classOutputMesagem;

    public RuleEngine()
    {
        fatos = new ArrayList<>();
        ruleModel = new ArrayList<>();
    }

    public boolean isResult()
    {
        return result;
    }

    public void setNotifications(boolean notifications)
    {
        this.notifications = notifications;
    }

    public boolean isNotifications()
    {
        return notifications;
    }

    public Class<?> getClassOutputMesagem()
    {
        return classOutputMesagem;
    }

    public void setClassOutputMesagem(Class<?> classOutputMesagem)
    {
        this.classOutputMesagem = classOutputMesagem;
    }

    public String getMensagemChecking()
    {
        return mensagemChecking;
    }

    public void setMensagemChecking(String mensagemChecking)
    {
        this.mensagemChecking = mensagemChecking;
    }

    public String getMensagemCheckTrue()
    {
        return mensagemCheckTrue;
    }

    public void setMensagemCheckTrue(String mensagemCheckTrue)
    {
        this.mensagemCheckTrue = mensagemCheckTrue;
    }

    public String getMensagemCheckFalse()
    {
        return mensagemCheckFalse;
    }

    public void setMensagemCheckFalse(String mensagemCheckFalse)
    {
        this.mensagemCheckFalse = mensagemCheckFalse;
    }

    public List<RuleModel> getRuleModel()
    {
        return ruleModel;
    }

    public void setRuleModel(List<RuleModel> ruleModel)
    {
        this.ruleModel = ruleModel;
    }

    public List<RuleFact> getFatos()
    {
        return fatos;
    }

    public void setFatos(List<RuleFact> fatos)
    {
        this.fatos = fatos;
    }

    // add ruleEngine

    public RuleEngine addRuleEngine(RuleEngine ruleEngine)
    {
        if (!ruleEngine.getFatos().isEmpty())
            this.getFatos().addAll(ruleEngine.getFatos());
        if (!ruleEngine.getRuleModel().isEmpty())
            this.getRuleModel().addAll(ruleEngine.getRuleModel());
        return this;
    }

    // check

    public boolean fireRules()
    {
        setNotifications(true);
        return runRules();
    }

    public boolean checkRules()
    {
        setNotifications(false);
        return runRules();
    }

    private boolean runRules()
    {
        showMensagemChecking();

        result = false;

        for (RuleModel ruleModel : ruleModel)
        {
            RuleEngineHelper.prepareFacts(ruleModel.getRule(), fatos);

            if (ruleModel.getMetodoRule().get(0).getNome().equals("@all"))
            {
                result = checarNotificacaoExecutantoAllMetodos(ruleModel.getRule());
            }
            else
            {
                result = checarNotificacaoExecutandoMedodo(ruleModel);
            }
            if (!result)
            {
                break;
            }
        }

        showMensagemCheck();
        return result;
    }

    // mensagemChecking

    private void showMensagemChecking()
    {

        showNotificacao(new Mensagem(mensagemChecking, MensagemTipo.INFO));

    }

    // mensagemCheckTrue mensagemCheckFalse

    private void showMensagemCheck()
    {

        if (result && mensagemCheckTrue != null)
        {
            showNotificacao(new Mensagem(mensagemCheckTrue, MensagemTipo.INFO));
        }
        else if (!result && mensagemCheckFalse != null)
        {
            showNotificacao(new Mensagem(mensagemCheckFalse, MensagemTipo.ERROR));
        }

    }

    // Notification

    private boolean checarNotificacaoExecutandoMedodo(RuleModel ruleModel)
    {
        boolean result = false;
        for (RuleMethod metodoRule : ruleModel.getMetodoRule())
        {
            result = checarNotificacao(RuleEngineHelper.execute(ruleModel.getRule(), metodoRule.getNome()));
            if (!result)
            {
                break;
            }
        }
        return result;
    }

    private boolean checarNotificacaoExecutantoAllMetodos(Object rule)
    {
        List<String> metodos = RuleEngineHelper.metodosNotificaveis(rule);
        checkNull(metodos, rule.getClass().getName());

        boolean result = false;
        for (String metodo : metodos)
        {
            result = checarNotificacao(RuleEngineHelper.execute(rule, metodo));
            if (!result)
            {
                break;
            }
        }
        return result;
    }

    private boolean checarNotificacao(Object result)
    {
        boolean retorno = false;

        if (result.getClass().equals(ArrayList.class))
        {
            if (!((ArrayList<?>) result).isEmpty())
            {
                if (((ArrayList<?>) result).get(0).getClass().equals(Notificacao.class))
                {
                    @SuppressWarnings("unchecked")
                    List<Notificacao> nots = ((ArrayList<Notificacao>) result);

                    for (Notificacao notificacao : nots)
                    {
                        retorno = notificacao.isResultado();
                        List<Mensagem> mensagens = notificacao.getMensagens();
                        showNoticacoes(mensagens, retorno);
                        if (!retorno)
                            break;
                    }
                }
            }
        }
        else if (result.getClass().equals(Notificacao.class))
        {
            retorno = ((Notificacao) result).isResultado();
            List<Mensagem> mensagens = ((Notificacao) result).getMensagens();
            showNoticacoes(mensagens, retorno);
        }
        else if (result.getClass().equals(Boolean.class))
        {
            retorno = (boolean) result;
        }
        else
        {
            myRuleException("MetodoRule deve retornar Boolean ou Notification");
        }
        return retorno;
    }

    // show notificacoes

    private void showNotificacao(Mensagem m)
    {
        if (isNotifications())
        {
            if (classOutputMesagem != null)
            {
                if (m.getTipo().equals(MensagemTipo.INFO) || m.getTipo().equals(MensagemTipo.EXPRESSAO_TRUE))
                {
                    RuleEngineHelper.executeNotificacao(classOutputMesagem, m.getTexto(), NotificacaoInfo.class);
                }
                else if (m.getTipo().equals(MensagemTipo.ADVERTENCIA))
                {
                    RuleEngineHelper.executeNotificacao(classOutputMesagem, m.getTexto(), NotificacaoWarn.class);
                }
                else if (m.getTipo().equals(MensagemTipo.ERROR) || m.getTipo().equals(MensagemTipo.EXPRESSAO_FALSE))
                {
                    RuleEngineHelper.executeNotificacao(classOutputMesagem, m.getTexto(), NotificacaoError.class);
                }
                else if ( !isResult() &&  m.getTipo().equals(MensagemTipo.CONTEXT) || m.getTipo().equals(MensagemTipo.CONTEXT))
                {
                    RuleEngineHelper.executeNotificacao(classOutputMesagem, m.getTexto(), NotificacaoContext.class);
                }
            }
            else
            {
                System.out.println("<<" + m.getTipo() + ">> " + m.getTexto());
            }
        }
    }

    private void showNoticacoes(List<Mensagem> mensagens, boolean retorno)
    {
        if (isNotifications())
            for (Mensagem m : mensagens)
            {
                if ((retorno && !m.getTipo().equals(MensagemTipo.EXPRESSAO_FALSE)) ||
                        (!retorno && !m.getTipo().equals(MensagemTipo.EXPRESSAO_TRUE)))
                {
                    showNotificacao(m);
                }
            }
    }

    // step 1

    public static InformeFato Builder()
    {
        return new Builder();
    }

    // step 2

    public interface InformeFato
    {
        InformeFato addFato(String nomeFato, Object objeto);

        InformeRule addClasseRule(Object rule);
    }

    // step 3

    public interface InformeRule
    {
        InformeAllRule addAllMetodoRule();

        InformeMetodo addMetodoRule(String nomeMetodo);
    }

    // step 4

    public interface InformeAllRule
    {
        InformeNovoRule addNovoClasseRule(Object rule);

        RuleEngine buildRules();
    }

    // step 5

    public interface InformeMetodo
    {
        InformeMetodo addMetodoRule(String nomeMetodo);

        InformeNovoRule addNovoClasseRule(Object rule);

        RuleEngine buildRules();

        boolean fireRules();

        boolean checkRules();
    }

    // step 6

    public interface InformeNovoRule
    {
        InformeAllRule addAllMetodoRule();

        InformeMetodo addMetodoRule(String nomeMetodo);
    }

    // step 7

    public interface BuildRules
    {
        boolean fireRules();

        boolean checkRules();
    }

    // builder

    public static class Builder
            implements InformeFato, InformeRule, InformeMetodo, InformeNovoRule, BuildRules, InformeAllRule
    {
        List<RuleFact> fatos;
        List<RuleModel> ruleModels;
        RuleModel ruleModel;
        Object rule;
        List<RuleMethod> metodoRule;

        public Builder()
        {
            ruleModels = new ArrayList<>();
            fatos = new ArrayList<>();
        }

        @Override
        public InformeFato addFato(String nomeFato, Object objeto)
        {
            verificarNomeValido(nomeFato);
            RuleFact fato = new RuleFact(nomeFato, objeto);
            fatos.add(fato);
            return this;
        }

        @Override
        public InformeRule addClasseRule(Object rule)
        {
            checkNull(rule, "Rule nao pode ser null!");
            iniciarRule(rule);
            return this;
        }

        @Override
        public InformeAllRule addAllMetodoRule()
        {
            metodoRule.add(new RuleMethod("@all"));
            return this;
        }

        @Override
        public InformeMetodo addMetodoRule(String nomeMetodo)
        {
            verificarNomeValido(nomeMetodo);
            verificarNomeMetodoRepetido(nomeMetodo);
            verificarMetodoNotificavel(nomeMetodo);
            metodoRule.add(new RuleMethod(nomeMetodo));
            return this;
        }

        @Override
        public InformeNovoRule addNovoClasseRule(Object rule)
        {
            checkNull(rule, "Rule nao pode ser null!");
            adicionarRuleModel();
            iniciarRule(rule);
            return this;
        }

        @Override
        public RuleEngine buildRules()
        {
            adicionarRuleModel();
            RuleEngine re = new RuleEngine();
            re.setRuleModel(ruleModels);
            re.setFatos(fatos);
            return re;
        }

        @Override
        public boolean fireRules()
        {
            RuleEngine re = new RuleEngine();
            re.setRuleModel(ruleModels);
            re.setFatos(fatos);
            return re.fireRules();
        }

        @Override
        public boolean checkRules()
        {
            RuleEngine re = new RuleEngine();
            re.setRuleModel(ruleModels);
            re.setFatos(fatos);
            return re.checkRules();
        }

        private void adicionarRuleModel()
        {
            if (rule != null)
            {
                RuleModel ruleModel = new RuleModel();
                ruleModel.setRule(rule);
                ruleModel.setMetodoRule(metodoRule);
                ruleModels.add(ruleModel);
            }
        }

        private void iniciarRule(Object rule)
        {
            this.rule = rule;
            metodoRule = new ArrayList<>();
        }

        private void verificarNomeValido(String nomeMetodo)
        {
            if (nomeMetodo == null || !isJavaMethodName(nomeMetodo))
            {
                myRuleMethodNameException(nomeMetodo);
            }
        }

        private void verificarNomeMetodoRepetido(String nomeMetodo)
        {
            if (metodoRule.indexOf((Object) nomeMetodo) > -1)
            {
                myRuleMethodNameRepetitionException(nomeMetodo);
            }
        }

        private void verificarMetodoNotificavel(String nomeMetodo)
        {
            checkIsTrue(metodoNotificavel(rule, nomeMetodo),
                    "Metodo [" + nomeMetodo + "] nao declarado  em " + rule.getClass());
        }

    }

}

package com.idomine.model.rules;

import static com.idomine.model.helper.ExpressaoLogicaHelper.maiorOuIgualQue;

import java.math.BigDecimal;

import com.idomine.model.Entidade;
import com.idomine.model.Fatura;
import com.idomine.ruleengine.annotations.Condition;
import com.idomine.ruleengine.annotations.InjectFact;
import com.idomine.ruleengine.notification.Notificacao;

public class FaturaRule
{
    @InjectFact(name = "fatura")
    public Fatura fatura;

    @InjectFact(name = "entidade")
    private Entidade entidade;

    @InjectFact(name = "valorMinimo")
    private BigDecimal valor;

    @Condition(prioridade=1)
    public Notificacao validarValor()
    {
        boolean regra = maiorOuIgualQue(fatura.getValor(), valor);
        return new Notificacao()
                .expressaoLogica(regra)
                .addMensagemInfo("1 Checando valor fatura")
                //.addMensagemInfo("1 Valor fatura dentro do limite de credito!")
                //.addMensagemAdvertencia("Teste msg warning!")
                //.addMensagemErro("1 Teste msg erro!")
                //.addMensagemTrue("1 Valor fatura passou na checagem")
                .addMensagemFalse("1 valor fatura deve ser maior ou igual a " + valor);

    }

    @Condition(prioridade=2)
    public Notificacao validarEntidade()
    {
        boolean regra = fatura.getEntidade() != null;
        return new Notificacao()
                .expressaoLogica(regra)
                .addMensagemInfo("2 Checando Entidade fatura")
                .addMensagemFalse("2 Informe uma entidade para fatura.");
    }

    @Condition(prioridade=3)
    public Notificacao validarData()
    {
        boolean regra = fatura.getEmissao() != null;

        return new Notificacao()
                .expressaoLogica(regra)
                .addMensagemInfo("2 Checando Data fatura")
                .addMensagemFalse("3 Informe data emissao da fatura");
    }

    @Condition(prioridade=4)
    public Notificacao validarListaRegras()
    {
        Notificacao notificacao = new Notificacao();
        Notificacao regraUm = regraUm();
        
        //criar metodos fluente para rule
        notificacao.setNotificationContext( regraUm.getNotificationContext() );
        notificacao.setMensagens(regraUm.getMensagens());
        notificacao.setResultado(regraUm.isResultado());
        
        if (notificacao.isResultado())
        {
            notificacao.setNotificationContext( regraDois().getNotificationContext() );
            notificacao.getMensagens().addAll( regraDois().getMensagens());
            notificacao.setResultado(regraDois().isResultado());
        }
        
        return notificacao;
    }

    private Notificacao regraUm()
    {
        boolean regra = true;
        return new Notificacao()
                .expressaoLogica(regra)
                .addMensagemInfo("4 Regra 1 checada")
                .addMensagemFalse("4 Regra 1 da fatura falhou");
    }

    private Notificacao regraDois()
    {
        boolean regra = false;
        return new Notificacao()
                .expressaoLogica(regra)
                .addMensagemInfo("5 Regra 2 checada")
                .addMensagemFalse("5 Regra 2 da fatura falhou")
                .addMensagemContext("regradois");
    }

}

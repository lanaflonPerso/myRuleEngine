package com.idomine.model.rules;

import static com.idomine.model.helper.ExpressaoLogicaHelper.maiorOuIgualQue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.idomine.model.Entidade;
import com.idomine.model.Fatura;
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

    public Notificacao validarValor()
    {
        boolean regra = false;//maiorOuIgualQue(fatura.getValor(), valor);
        return new Notificacao()
                .expressaoLogica(regra)
                .addMensagemInfo("Checando valor fatura")
                .addMensagemInfo("Valor fatura dentro do limite de credito!")
                .addMensagemAdvertencia("Teste msg warning!")
                .addMensagemErro("Teste msg erro!")
                .addMensagemTrue("Valor fatura passou na checagem")
                .addMensagemFalse("valor fatura deve ser maior ou igual a " + valor);

    }

    public Notificacao validarEntidade()
    {
        boolean regra = fatura.getEntidade() != null;
        return new Notificacao()
                .expressaoLogica(regra)
                .addMensagemInfo("Entidade fatura checado")
                .addMensagemFalse("Informe uma entidade para fatura.");
    }

    public Notificacao validarData()
    {
        boolean regra = fatura.getEmissao() != null;

        return new Notificacao()
                .expressaoLogica(regra)
                .addMensagemFalse("Informe data emissao da fatura");
    }

    public List<Notificacao> validarListaRegras()
    {
        List<Notificacao> notificacoes = new ArrayList<>();
        Notificacao regraUm = regraUm();
        notificacoes.add(regraUm);
        if (regraUm.isResultado())
            notificacoes.add(regraDois());
        return notificacoes;
    }

    private Notificacao regraUm()
    {
        boolean regra = true;
        return new Notificacao()
                .expressaoLogica(regra)
                .addMensagemInfo("regra 1 checada")
                .addMensagemFalse("Regra 1 da fatura falhou");
    }

    private Notificacao regraDois()
    {
        boolean regra = true;
        return new Notificacao()
                .expressaoLogica(regra)
                .addMensagemInfo("Regra 2 checada")
                .addMensagemFalse("Regra 2 da fatura falhou");
    }

}

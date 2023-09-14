package br.com.constance.portal.fiscal;

import br.com.constance.util.MensagemUtils;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.modelcore.comercial.impostos.ImpostosHelpper;

import java.math.BigDecimal;


public class RecalculaImpostosAcao implements AcaoRotinaJava {
    private BigDecimal numeroUnicoNota;

    @Override
    public void doAction(ContextoAcao contexto) throws Exception {

        Registro[] linhas = contexto.getLinhas();
        if (linhas.length == 0) {
            MensagemUtils.disparaErro("Selecione ao menos uma linha.");
        }
        for (Registro linha : linhas) {
            numeroUnicoNota = new BigDecimal(linha.getCampo("NUNOTA").toString());
            ImpostosHelpper impostosHelpper = new ImpostosHelpper();
            impostosHelpper.setForcarRecalculo(true);
            impostosHelpper.calcularImpostos(numeroUnicoNota);

        }
        contexto.setMensagemRetorno("Recalculo do imposto realizado com sucesso!");
    }
}

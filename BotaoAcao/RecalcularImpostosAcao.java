package BotaoAcao;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.modelcore.comercial.impostos.ImpostosHelpper;

import java.math.BigDecimal;


public class RecalcularImpostosAcao implements AcaoRotinaJava {

    @Override
    public void doAction(ContextoAcao contexto) throws Exception {
        Registro[] linhas = contexto.getLinhas();

        if (linhas.length == 0) {
            throw new Exception("Selecione ao menos uma linha.");
        }

        for (Registro linha : linhas) {
            BigDecimal numeroUnicoNota = new BigDecimal(linha.getCampo("NUNOTA").toString());
            try {
                ImpostosHelpper impostosHelpper = new ImpostosHelpper();
                impostosHelpper.setForcarRecalculo(true);
                impostosHelpper.calcularImpostos(numeroUnicoNota);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
        contexto.setMensagemRetorno("Impostos recalculados com sucesso!");
    }
}

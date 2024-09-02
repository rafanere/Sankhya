package EventoProgramavel;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.comercial.CentralFinanceiro;
import br.com.sankhya.modelcore.comercial.impostos.ImpostosHelpper;

import java.math.BigDecimal;

public class RecalcularValoresEvento implements EventoProgramavelJava {

    private void recalcularImpostos(BigDecimal numeroUnico) throws Exception {
        ImpostosHelpper impostosHelpper = new ImpostosHelpper();
        impostosHelpper.setForcarRecalculo(true);
        impostosHelpper.calcularImpostos(numeroUnico);
    }

    private void refazerFinanceiro(BigDecimal numeroUnico) throws Exception {
        CentralFinanceiro centralFinanceiro = new CentralFinanceiro();
        centralFinanceiro.inicializaNota(numeroUnico);
        centralFinanceiro.refazerFinanceiro();
    }

    @Override
    public void beforeInsert(PersistenceEvent event) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent event) throws Exception {

    }

    @Override
    public void beforeDelete(PersistenceEvent event) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent event) throws Exception {

    }

    @Override
    public void afterUpdate(PersistenceEvent event) throws Exception {
        DynamicVO eventoVO = (DynamicVO) event.getVo();
        BigDecimal numeroUnico = eventoVO.asBigDecimal("NUNOTA");
        boolean temFinanceiro = JapeFactory.dao("Financeiro").findOne("NUNOTA = ?", numeroUnico) != null;

        if (eventoVO.asBigDecimal("CabecalhoNota.CODTIPOPER").equals(BigDecimal.valueOf(3199))) {
            if (event.getModifingFields().isModifingAny("VLRUNIT") && (event.getModifingFields().isModifingAny("VLRTOT")) && (event.getModifingFields().isModifingAny("AD_PRECOATUALIZADO"))) {
                recalcularImpostos(numeroUnico);
                if (temFinanceiro) {
                    refazerFinanceiro(numeroUnico);
                }
            }
        }
    }

    @Override
    public void afterDelete(PersistenceEvent event) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext tranCtx) throws Exception {

    }
}

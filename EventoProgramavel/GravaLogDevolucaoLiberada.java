package br.com.constance.financeiro.devolucao;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;

import java.math.BigDecimal;

public class GravaLogDevolucaoLiberadaEvento implements EventoProgramavelJava {
    private final JapeWrapper liberacaoLimiteDAO = JapeFactory.dao("LiberacaoLimite");
    private final JapeWrapper logDevolucoesDAO = JapeFactory.dao("AD_TSILOGDEV");
    private final JapeWrapper filaServicoDAO = JapeFactory.dao("AD_TSIFILSER");
    private final JapeWrapper cabecalhoLogisticaReversaDAO = JapeFactory.dao("AD_TGFLRECAB");
    private final JapeWrapper historicoLogisticaReversaDAO = JapeFactory.dao("AD_TGFLRESTATUS");
    private DynamicVO devolucaoVO;
    private BigDecimal numeroUnicoDevolucao;
    private BigDecimal numeroUnicoLogisticaReversa;
    private Boolean liberouLimite = false;
    private Boolean ehLogisticaReversa = false;
    private PersistenceEvent persistenceEvent;

    private void buscaDevolucaoLiberada() throws Exception {
        devolucaoVO = liberacaoLimiteDAO.findOne("TABELA = 'TGFCAB' AND NUCHAVE = ?", String.valueOf(numeroUnicoDevolucao));
        if (devolucaoVO != null) {
            FluidCreateVO logDevolucoesFCVO = logDevolucoesDAO.create();
            logDevolucoesFCVO.set("NUNOTA", numeroUnicoDevolucao);
            logDevolucoesFCVO.save();
        }
    }

    private void verificaDevolucaoNaLogisticaReversa() throws Exception {
        devolucaoVO = cabecalhoLogisticaReversaDAO.findOne("NUNOTADEVOL = ?", numeroUnicoDevolucao);
        if (devolucaoVO != null) {
            ehLogisticaReversa = true;
            numeroUnicoLogisticaReversa = devolucaoVO.asBigDecimal("NUNICO");
        } else {
            ehLogisticaReversa = false;
        }
    }

    private void inserirDevolucaoFilaServico() throws Exception {
        filaServicoDAO.create()
                .set("NUTIPSER", new BigDecimal(13))
                .set("DADOS", numeroUnicoLogisticaReversa.toString())
                .save();
    }

    private void atualizaHistoricoLogisticaReversa() throws Exception {
        historicoLogisticaReversaDAO.create()
                .set("NUNICO", numeroUnicoLogisticaReversa)
                .set("STATUS", "FA")
                .save();
    }

    private void processar() throws Exception {
        devolucaoVO = (DynamicVO) persistenceEvent.getVo();
        numeroUnicoDevolucao = devolucaoVO.asBigDecimalOrZero("NUCHAVE");
        BigDecimal codigoEvento = devolucaoVO.asBigDecimalOrZero("EVENTO");
        if (codigoEvento.equals(BigDecimal.valueOf(1016))) {
            buscaDevolucaoLiberada();
            verificaDevolucaoNaLogisticaReversa();
            if (ehLogisticaReversa) {
                inserirDevolucaoFilaServico();
                atualizaHistoricoLogisticaReversa();
            }
        }
    }

    @Override
    public void beforeInsert(PersistenceEvent event) throws Exception {

    }

    @Override
    public void beforeUpdate(PersistenceEvent event) throws Exception {
        liberouLimite = event.getModifingFields().containsKey("DHLIB");

    }

    @Override
    public void beforeDelete(PersistenceEvent event) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent event) throws Exception {

    }

    @Override
    public void afterUpdate(PersistenceEvent event) throws Exception {
        this.persistenceEvent = event;
        if (liberouLimite) {
            processar();
        }
    }

    @Override
    public void afterDelete(PersistenceEvent event) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext tranCtx) throws Exception {

    }
}

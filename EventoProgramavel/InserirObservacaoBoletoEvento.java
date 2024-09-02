package EventoProgramavel;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class InserirObservacaoBoletoEvento implements EventoProgramavelJava {
    private String observacao = null;
    private Integer codigoNatureza = 0;
    private final JapeWrapper naturezaDAO = JapeFactory.dao("Natureza");
    private DynamicVO financeiroVO;
    private Boolean tipoTituloBoleto = false;


    private void insereObservacaoEmContrato(DynamicVO financeiroVO) throws Exception {
        BigDecimal numeroContrato = financeiroVO.asBigDecimalOrZero("NUMCONTRATO");

        if (numeroContrato.compareTo(BigDecimal.ZERO) > 0) {
            codigoNatureza = financeiroVO.asInt("CODNAT");

            Timestamp dataNegociacao = financeiroVO.asTimestamp("DTNEG");
            String descricaoNatureza = naturezaDAO.findByPK(codigoNatureza).asString("DESCRNAT");
            SimpleDateFormat format = new SimpleDateFormat("MM/yyyy");
            String dataFormatada = format.format(dataNegociacao);
            String tipoNatureza;
            if (descricaoNatureza.contains("ROYALT")) {
                tipoNatureza = "ROYALTIES";
            } else if (descricaoNatureza.contains("PROMO")) {
                tipoNatureza = "FPP";
            } else {
                tipoNatureza = "";
            }
            observacao = tipoNatureza + " COMP. " + dataFormatada;
        }
        financeiroVO.setProperty("AD_MSGBOL", observacao);
    }

    private void insereObservacaoPorNatureza(DynamicVO financeiroVO) throws Exception {
        codigoNatureza = financeiroVO.asInt("CODNAT");
        switch (codigoNatureza) {
            case 10010110:
                observacao = "Cobrança referente ao Fundo de Promoção.";
                break;
            case 10010119:
                observacao = "Cobrança referente à Bornlogic - Mensalidade/Crédito patrocinado.";
                break;
            case 10010117:
                observacao = "Cobrança referente ao acordo de Fundo de Promoção";
                break;
            case 10010107:
                observacao = "Cobrança referente aos Royalties.";
                break;
            case 10010113:
                observacao = "Cobrança referente ao acordo de Royalties.";
                break;
            case 10010114:
                observacao = "Cobrança referente ao acordo de Taxa de Franquia.";
                break;
            case 10010116:
                observacao = "Cobrança referente ao acordo de Taxa Master.";
                break;
            case 10010108:
                observacao = "Cobrança referente à Taxa de Franquia.";
                break;
            case 10010109:
                observacao = "Cobrança referente à Taxa de Franquia Master.";
                break;
            case 10010101:
                observacao = "Cobrança referente a compra de mercadorias Tecnologística.";
                break;
            case 10010106:
                observacao = "Cobrança referente a comissão de vendas Omnichannel.";
                break;
            case 10010120:
                observacao = "Cobrança referente à VTEX LOG.";
                break;
            default:
                observacao = "";
        }
        financeiroVO.setProperty("AD_MSGBOL", observacao);
    }

    private void confereSeEhBoleto(DynamicVO financeiroVO) throws Exception {
        int tipoTitulo = financeiroVO.asInt("CODTIPTIT");

        if (tipoTitulo == 4)
            tipoTituloBoleto = true;
    }

    @Override
    public void beforeInsert(PersistenceEvent event) throws Exception {
        financeiroVO = (DynamicVO) event.getVo();
        confereSeEhBoleto(financeiroVO);
        if (tipoTituloBoleto) {
            insereObservacaoEmContrato(financeiroVO);
            insereObservacaoPorNatureza(financeiroVO);
        }
    }

    @Override
    public void beforeUpdate(PersistenceEvent event) throws Exception {
        financeiroVO = (DynamicVO) event.getVo();
        confereSeEhBoleto(financeiroVO);
        if (tipoTituloBoleto && event.getModifingFields().isModifing("CODNAT")) {
            insereObservacaoEmContrato(financeiroVO);
            insereObservacaoPorNatureza(financeiroVO);
        }
    }

    @Override
    public void beforeDelete(PersistenceEvent event) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent event) throws Exception {

    }

    @Override
    public void afterUpdate(PersistenceEvent event) throws Exception {

    }

    @Override
    public void afterDelete(PersistenceEvent event) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext tranCtx) throws Exception {

    }
}
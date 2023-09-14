package br.com.constance.financeiro.conciliacao;

import br.com.constance.util.MensagemUtils;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.comercial.AtributosRegras;

import java.math.BigDecimal;
import java.util.Objects;

public class ValidaContaPorEmpresaEvento implements EventoProgramavelJava {
    private BigDecimal codigoEmpresa;
    private BigDecimal codigoContaBancaria;
    private BigDecimal codigoTipoOperacao;
    private DynamicVO contaEmpresaVO;
    private DynamicVO tsictempVo;
    private Boolean validado = false;
    private final JapeWrapper contaPorEmpresaDAO = JapeFactory.dao("AD_TSICTEMP");

    private void validaRegras(BigDecimal codigoTipoOperacao) throws Exception {
        if (Objects.equals(codigoTipoOperacao, BigDecimal.valueOf(3275))) {
            validado = false;
        } else if (JapeSession.getProperty(AtributosRegras.NUNOTA_SENDO_DUPLICADA) != null) {
            validado = false;
        } else
            validado = true;
    }

    private void buscaContaPorEmpresa(DynamicVO contaEmpresaVO) throws Exception {
        codigoTipoOperacao = contaEmpresaVO.asBigDecimal("CODTIPOPER");
        validaRegras(codigoTipoOperacao);

        if (validado) {
            codigoEmpresa = contaEmpresaVO.asBigDecimal("CODEMP");
            codigoContaBancaria = contaEmpresaVO.asBigDecimalOrZero("CODCTABCOINT");

            if (Objects.equals(codigoContaBancaria, BigDecimal.ZERO))
                return;

            tsictempVo = contaPorEmpresaDAO.findOne("CODEMP = ? AND CODCTABCOINT = ?", codigoEmpresa, codigoContaBancaria);
            if (tsictempVo == null) {
                MensagemUtils.disparaErro("A conta " + codigoContaBancaria + " não pode ser utilizada com a empresa " + codigoEmpresa
                        + ". <br> Por gentileza verificar a relação de Contas Bancárias x Empresa na tela \"Empresa\".");
            } else
                return;
        } else
            return;

    }

    @Override
    public void beforeInsert(PersistenceEvent event) throws Exception {
        contaEmpresaVO = (DynamicVO) event.getVo();
        try {
            buscaContaPorEmpresa(contaEmpresaVO);
        } catch (Exception e) {
            MensagemUtils.disparaErro(e.getMessage());
        }
    }

    @Override
    public void beforeUpdate(PersistenceEvent event) throws Exception {
        contaEmpresaVO = (DynamicVO) event.getVo();
        try {
            buscaContaPorEmpresa(contaEmpresaVO);
        } catch (Exception e) {
            MensagemUtils.disparaErro(e.getMessage());
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
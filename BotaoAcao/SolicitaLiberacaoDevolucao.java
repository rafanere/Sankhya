package br.com.constance.portal.financeiro;

import br.com.constance.util.MensagemUtils;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.comercial.LiberacaoAlcadaHelper;
import br.com.sankhya.modelcore.comercial.LiberacaoSolicitada;

import java.math.BigDecimal;

public class SolicitarLiberacaoDeDevolucao implements AcaoRotinaJava {
    JapeWrapper liberacaoLimiteDAO = JapeFactory.dao("LiberacaoLimite");
    DynamicVO liberacaoLimiteVO;
    BigDecimal numeroUnico;
    BigDecimal numeroNota;
    BigDecimal valorNota;
    Boolean liberacaoExiste;
    LiberacaoSolicitada liberacaoSolicitada;

    private void verificaLiberacaoExistente() throws Exception {
        liberacaoLimiteVO = liberacaoLimiteDAO.findOne("TABELA = 'TGFCAB' AND NUCHAVE = ?", String.valueOf(numeroUnico));
        liberacaoExiste = liberacaoLimiteVO != null;
    }

    private void criarSolicitacaoDeLiberacao() throws Exception {
        liberacaoSolicitada = new LiberacaoSolicitada(
                numeroUnico,
                "TGFCAB",
                1016,
                BigDecimal.ZERO,
                ("Nota: " + numeroNota),
                BigDecimal.ZERO,
                valorNota,
                valorNota,
                BigDecimal.ZERO,
                AuthenticationInfo.getCurrent().getUserID()
        );
        LiberacaoAlcadaHelper.inserirSolicitacao(liberacaoSolicitada);
        LiberacaoAlcadaHelper.processarLiberacao(liberacaoSolicitada);
        liberacaoExiste = true;
    }

    private void removeSolicitacaoDeLiberacaoPendente() throws Exception {
        verificaLiberacaoExistente();
        if (liberacaoExiste) {
            LiberacaoAlcadaHelper.removeSolicitacoesPendentes(numeroUnico, "TGFCAB");
            liberacaoExiste = false;
        }
    }

    @Override
    public void doAction(ContextoAcao contexto) throws Exception {
        Registro[] linhas = contexto.getLinhas();
        if (linhas.length == 0)
            MensagemUtils.disparaErro("Selecione ao menos um registro.");
        else {
            for (Registro linha : linhas) {
                numeroUnico = (BigDecimal) linha.getCampo("NUNOTA");
                numeroNota = (BigDecimal) linha.getCampo("NUMNOTA");
                valorNota = (BigDecimal) linha.getCampo("VLRNOTA");
                verificaLiberacaoExistente();
                if (!liberacaoExiste) {
                    criarSolicitacaoDeLiberacao();
                } else {
                    removeSolicitacaoDeLiberacaoPendente();
                    criarSolicitacaoDeLiberacao();
                }
                liberacaoExiste = false;
            }
            contexto.setMensagemRetorno("Pedido de liberação criado!");
        }
    }
}
package br.com.constance.financeiro.conciliacao;

import br.com.constance.util.MensagemUtils;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.financeiro.helper.BaixaHelper;
import br.com.sankhya.modelcore.financeiro.util.DadosBaixa;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BaixarFinanceiroAcao implements AcaoRotinaJava {
    private BigDecimal numeroUnicoFinanceiro;
    private BigDecimal valorTotalBaixa = BigDecimal.ZERO;
    private BigDecimal valorLiquido = BigDecimal.ZERO;
    private Timestamp dataVencimentoSankhya;
    private Timestamp dataVencimentoAdquirente;
    private Registro[] linhas;
    private final JapeWrapper financeiroDAO = JapeFactory.dao("Financeiro");
    private final List<BigDecimal> listaNumerosFinanceiro = new ArrayList<>();
    private final Collection<DadosBaixa> dadosBaixaAgrupada = new ArrayList<>();
    private BaixaHelper baixaHelper;


    @Override
    public void doAction(ContextoAcao contexto) throws Exception {
        linhas = contexto.getLinhas();
        if (linhas.length == 0) {
            MensagemUtils.disparaErro("Selecione pelo menos uma linha.");
        }
        processar();
        baixarFinanceiros();
        contexto.setMensagemRetorno("Registros baixados!");
    }

    private void processar() throws Exception {
        for (Registro linha : linhas) {
            numeroUnicoFinanceiro = new BigDecimal(linha.getCampo("NUFIN").toString());
            dataVencimentoSankhya = (Timestamp) linha.getCampo("DTVENCSNK");
            dataVencimentoAdquirente = (Timestamp) linha.getCampo("DTVENCAD");
            valorLiquido = new BigDecimal(linha.getCampo("VLRLIQUIDOSNK").toString());
            validarRegistros();
        }
    }

    private void validarRegistros() throws Exception {
        DynamicVO financeiroVO = financeiroDAO.findByPK(numeroUnicoFinanceiro);
        if ((financeiroVO != null) && (dataVencimentoSankhya.equals(dataVencimentoAdquirente))) {
            listaNumerosFinanceiro.add(numeroUnicoFinanceiro);
            valorTotalBaixa = valorTotalBaixa.add(valorLiquido);
        }
    }

    private void baixarFinanceiros() throws Exception {
        for (BigDecimal numeroFinanceiro : listaNumerosFinanceiro) {
            baixaHelper = new BaixaHelper(numeroFinanceiro, AuthenticationInfo.getCurrentOrNull().getUserID(), TimeUtils.getNow(), false);
            baixaHelper.setBaixaAgrupada(true);
            baixaHelper.setBaixandoPelaBaixaAutomatica(true);
            baixaHelper.setManterJurosMulta(false);
            DadosBaixa dadosBaixa = baixaHelper.montaDadosBaixa(TimeUtils.getNow(), true, false, TimeUtils.getNow());
            dadosBaixa.getDadosAdicionais().setCodTipoOperacao(BigDecimal.valueOf(4300));
            dadosBaixa.getDadosBancarios().setCodLancamento(BigDecimal.ONE);
            dadosBaixa.getDadosBancarios().setCodConta(BigDecimal.valueOf(996));
            dadosBaixa.setCalculoJuro(false);
            dadosBaixaAgrupada.add(dadosBaixa);
        }
        baixaHelper.baixaAgrupada(dadosBaixaAgrupada, valorTotalBaixa.doubleValue());
    }
}
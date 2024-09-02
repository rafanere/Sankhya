package BotaoAcao;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import com.sankhya.util.StringUtils;
import com.sankhya.util.TimeUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class LimparDuplicataAcao implements AcaoRotinaJava {
    private final JapeWrapper financeiroDAO = JapeFactory.dao("Financeiro");
    private BigDecimal numeroUnicoFinanceiro;
    private Registro[] linhas;
    private final List<BigDecimal> listaNumerosPraLimpar = new ArrayList<>();
    private final List<BigDecimal> listaNumerosComErro = new ArrayList<>();
    private ContextoAcao contextoAcao;

    @Override
    public void doAction(ContextoAcao contexto) throws Exception {
        this.contextoAcao = contexto;
        linhas = contexto.getLinhas();
        if (linhas.length == 0) {
            throw new Exception("Selecione pelo menos uma linha.");
        }
        try {
            processar();
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    private void processar() throws Exception {
        for (Registro linha : linhas) {
            numeroUnicoFinanceiro = (BigDecimal) linha.getCampo("NUFIN");
            validaRegras();
        }
        desfazDuplicatas();
        if (listaNumerosComErro.isEmpty()) {
            contextoAcao.setMensagemRetorno("Duplicatas desfeitas com sucesso!");
        } else {
            contextoAcao.setMensagemRetorno("Houve erro para limpar as duplicatas dos seguintes números únicos:<br>".concat(StringUtils.substr(listaNumerosComErro.toString(), 1, listaNumerosComErro.toString().length() - 1)).concat(". <br>Verifique se existe alguma renegociação vinculada ou se a movimentação ainda existe."));
        }
    }

    private void validaRegras() throws Exception {
        DynamicVO financeiroVO = financeiroDAO.findByPK(numeroUnicoFinanceiro);
        if (financeiroVO != null) {
            BigDecimal numeroRenegociacao = financeiroVO.asBigDecimal("NURENEG");
            if (numeroRenegociacao != null) {
                listaNumerosComErro.add(numeroUnicoFinanceiro);
            } else
                listaNumerosPraLimpar.add(numeroUnicoFinanceiro);
        } else {
            listaNumerosComErro.add(numeroUnicoFinanceiro);
        }
    }

    private void desfazDuplicatas() throws Exception {
        for (BigDecimal numeroFinanceiro : listaNumerosPraLimpar) {
            FluidUpdateVO financeiroFVO = financeiroDAO.prepareToUpdateByPK(numeroFinanceiro);
            financeiroFVO.set("DTALTER", TimeUtils.getNow());
            financeiroFVO.set("CODUSU", AuthenticationInfo.getCurrent().getUserID());
            financeiroFVO.set("NUMDUPL", null);
            financeiroFVO.update();
        }
    }
}
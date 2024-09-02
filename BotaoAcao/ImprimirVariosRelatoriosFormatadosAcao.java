package BotaoAcao;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.AgendamentoRelatorioHelper;
import br.com.sankhya.modelcore.util.AgendamentoRelatorioHelper.ParametroRelatorio;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.ws.ServiceContext;
import com.sankhya.util.SessionFile;

import java.math.BigDecimal;
import java.util.*;

public class ImprimirVariosRelatoriosFormatadosAcao implements AcaoRotinaJava {
    JapeWrapper parceiroDAO = JapeFactory.dao("Parceiro");
    Map<BigDecimal, BigDecimal> numerosUnicosParceiros = new HashMap<>();

    private Collection<BigDecimal> ordenarPorNomeParceiro(Map<BigDecimal, BigDecimal> numerosUnicosParceirosMap) throws Exception {
        List<Map.Entry<BigDecimal, String>> listaOrdenada = new ArrayList<>();

        for (Map.Entry<BigDecimal, BigDecimal> numeroUnicoParceiroMap : numerosUnicosParceirosMap.entrySet()) {
            BigDecimal numeroUnico = numeroUnicoParceiroMap.getKey();
            BigDecimal codigoParceiro = numeroUnicoParceiroMap.getValue();
            DynamicVO parceiroVO = parceiroDAO.findByPK(codigoParceiro);
            String nomeParceiro = parceiroVO.asString("NOMEPARC");
            listaOrdenada.add(new AbstractMap.SimpleEntry<>(numeroUnico, nomeParceiro));
        }

        listaOrdenada.sort(Map.Entry.comparingByValue());
        Collection<BigDecimal> numerosUnicosOrdenados = new ArrayList<>();
        for (Map.Entry<BigDecimal, String> lista : listaOrdenada) {
            numerosUnicosOrdenados.add(lista.getKey());
        }
        return numerosUnicosOrdenados;
    }

    private void imprimirRelatorio(ContextoAcao contextoAcao, Collection<BigDecimal> numerosUnicosOrdenados) throws Exception {
        BigDecimal numeroRelatorioFormatado = new BigDecimal(146);
        List<Object> listaParametros = new ArrayList<>();
        byte[] pdfBytes = null;
        String chave = "romaneiov2.pdf";

        try {
            EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();

            ParametroRelatorio parametroRelatorio = new ParametroRelatorio("PK_NUNOTAS", BigDecimal.class.getName(), numerosUnicosOrdenados);
            listaParametros.add(parametroRelatorio);

            pdfBytes = AgendamentoRelatorioHelper.getPrintableReport(numeroRelatorioFormatado, listaParametros, contextoAcao.getUsuarioLogado(), dwfFacade);
            SessionFile sessionFile = SessionFile.createSessionFile("romaneiov2.pdf", "Romaneio V2", pdfBytes);
            ServiceContext.getCurrent().putHttpSessionAttribute(chave, sessionFile);

            contextoAcao.setMensagemRetorno("<a id=\"alink\" href=\"/mge/visualizadorArquivos.mge?chaveArquivo="
                    + chave
                    + "\" target=\"_blank\">Baixar Arquivo</a>");
        } catch (Exception e) {
            throw new Exception("<b>" + e.getMessage() + "<br>" + e.getCause() + "</b>");
        }
    }

    @Override
    public void doAction(ContextoAcao ctx) throws Exception {
        Registro[] linhas = ctx.getLinhas();

        if (linhas.length == 0) {
            throw new Exception("Selecione ao menos uma linha.");
        }

        for (Registro linha : linhas) {
            BigDecimal numeroUnico = (BigDecimal) linha.getCampo("NUNOTA");
            BigDecimal codigoParceiro = (BigDecimal) linha.getCampo("CODPARC");
            numerosUnicosParceiros.put(numeroUnico, codigoParceiro);
        }

        Collection<BigDecimal> numerosUnicosOrdenados = ordenarPorNomeParceiro(numerosUnicosParceiros);
        imprimirRelatorio(ctx, numerosUnicosOrdenados);
    }
}

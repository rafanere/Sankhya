package Utilitarios;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailUtils {
    private JapeWrapper usuarioDAO = JapeFactory.dao("Usuario");
    private JapeWrapper emailDAO = JapeFactory.dao("MSDFilaMensagem");
    private JapeWrapper anexoMensagemDAO = JapeFactory.dao("AnexoMensagem");
    private JapeWrapper anexoPorMensagemDAO = JapeFactory.dao("AnexoPorMensagem");
    private BigDecimal contaSmtp;
    private BigDecimal numeroTentativasDeEnvio = new BigDecimal(2);
    private String assunto;
    private String mensagem;
    private String status;
    private String tipoEnvio;
    private List<String> destinatarios = new ArrayList<>();
    private Map<String, byte[]> listaAnexos = new HashMap<>();
    private DynamicVO emailVO;
    private DynamicVO anexoMensagemVO;
    private List<DynamicVO> listaMesagemEmails;
    private List<DynamicVO> listaMesagemAnexo;


    public void setContaSmtp(BigDecimal contaSmtp) {
        this.contaSmtp = contaSmtp;
    }

    public void setNumeroTentativasDeEnvio(BigDecimal numeroTentativasDeEnvio) {
        this.numeroTentativasDeEnvio = numeroTentativasDeEnvio;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTipoEnvio(String tipoEnvio) {
        this.tipoEnvio = tipoEnvio;
    }

    public void setDestinatarios(String email) throws Exception {
        this.destinatarios.add(email);
    }

    public void adicionarAnexo(String nomeAnexo, byte[] anexo) {
        listaAnexos.put(nomeAnexo, anexo);
    }

    public void setDestinatarios(BigDecimal codigoUsuario) throws Exception {
        String emailUsuario = usuarioDAO.findByPK(codigoUsuario).asString("EMAIL");
        if (emailUsuario != null) {
            this.destinatarios.add(emailUsuario);
        }
    }

    public void processar() throws Exception {
        criarEmail();
        criarAnexo();
        vincularAnexoComEmail();
    }


    private void criarEmail() throws Exception {
        listaMesagemEmails = new ArrayList<>();

        for (String destinatario : destinatarios) {
            FluidCreateVO fluidCreateVO = emailDAO.create();
            fluidCreateVO.set("CODCON", BigDecimal.ZERO);
            fluidCreateVO.set("CODSMTP", contaSmtp);
            fluidCreateVO.set("EMAIL", destinatario);
            fluidCreateVO.set("ASSUNTO", assunto);
            fluidCreateVO.set("MENSAGEM", mensagem.toCharArray());
            fluidCreateVO.set("DTENTRADA", new Timestamp(System.currentTimeMillis()));
            fluidCreateVO.set("STATUS", "Pendente");
            fluidCreateVO.set("TIPOENVIO", "E");
            fluidCreateVO.set("MAXTENTENVIO", numeroTentativasDeEnvio);
            emailVO = fluidCreateVO.save();
            listaMesagemEmails.add(emailVO);
        }
    }

    private void criarAnexo() throws Exception {
        listaMesagemAnexo = new ArrayList<>();

        for (Map.Entry<String, byte[]> arquivo : listaAnexos.entrySet()) {
            FluidCreateVO fluidCreateVO = anexoMensagemDAO.create();
            fluidCreateVO.set("TIPO", "application/octet-stream");
            fluidCreateVO.set("NOMEARQUIVO", arquivo.getKey());
            fluidCreateVO.set("ANEXO", arquivo.getValue());
            anexoMensagemVO = fluidCreateVO.save();
            listaMesagemAnexo.add(anexoMensagemVO);
        }
    }

    private void vincularAnexoComEmail() throws Exception {
        for (DynamicVO anexoVO : listaMesagemAnexo) {
            for (DynamicVO emailsMensagemVO : listaMesagemEmails) {

                FluidCreateVO fluidCreateVO = anexoPorMensagemDAO.create();
                fluidCreateVO.set("CODFILA", emailsMensagemVO.asBigDecimalOrZero("CODFILA"));
                fluidCreateVO.set("NUANEXO", anexoVO.asBigDecimalOrZero("NUANEXO"));
                fluidCreateVO.save();
            }
        }
    }
}
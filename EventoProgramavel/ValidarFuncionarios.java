package EventosProgramados;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;

public class ValidarFuncionarios implements EventoProgramavelJava {
    private Boolean cadastroValidado = false;
    private Integer codigoCategoria;
    private Integer codigoVinculo;

    private void validaVinculoCategoriaFuncionario() throws Exception {
        if (codigoVinculo == 2 && codigoCategoria == 901) {
            cadastroValidado = true;
        } else if (codigoVinculo == 55 && codigoCategoria == 103) {
            cadastroValidado = true;
        } else if (codigoVinculo == 35) {
            switch (codigoCategoria) {
                case 303:
                case 306:
                case 309: {
                    cadastroValidado = true;
                    break;
                }
            }
        } else if (codigoVinculo == 40) {
            switch (codigoCategoria) {
                case 201:
                case 202: {
                    cadastroValidado = true;
                    break;
                }
            }
        } else if (codigoVinculo == 50) {
            switch (codigoCategoria) {
                case 105:
                case 106: {
                    cadastroValidado = true;
                    break;
                }
            }
        } else if (codigoVinculo == 90) {
            switch (codigoCategoria) {
                case 701:
                case 711:
                case 712:
                case 741: {
                    cadastroValidado = true;
                    break;
                }
            }
        } else {
            cadastroValidado = false;
        }
    }

    @Override
    public void beforeInsert(PersistenceEvent event) throws Exception {
        DynamicVO funcionarioVO = (DynamicVO) event.getVo();
        codigoVinculo = funcionarioVO.asInt("VINCULO");
        codigoCategoria = funcionarioVO.asInt("CODCATEGESOCIAL");
        validaVinculoCategoriaFuncionario();
        if (!cadastroValidado) {
            throw new Exception("Não é possível cadastrar funcionário utilizando a Categoria para o e-Social " + codigoCategoria.toString() + " com o Vínculo " + codigoVinculo.toString());
        }
    }

    @Override
    public void beforeUpdate(PersistenceEvent event) throws Exception {
        DynamicVO funcionarioVO = (DynamicVO) event.getVo();
        codigoVinculo = funcionarioVO.asInt("VINCULO");
        codigoCategoria = funcionarioVO.asInt("CODCATEGESOCIAL");
        validaVinculoCategoriaFuncionario();
        if (!cadastroValidado) {
            throw new Exception("Não é possível alterar informações de funcionário que esteja utilizando a Categoria para o e-Social " + codigoCategoria + " com o Vínculo " + codigoVinculo + ". <br> Corrija a informação antes de prosseguir com a devida alteração.");
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

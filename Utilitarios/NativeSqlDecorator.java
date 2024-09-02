package Utilitarios;

import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class NativeSqlDecorator extends Thread {
    private NativeSql nativeSql;
    private String sql;
    private boolean aberto = false;
    ResultSet resultSet;


    public NativeSqlDecorator() {
        iniciar();
    }

    public NativeSqlDecorator(String sql) {
        iniciar();
        nativeSql.appendSql(sql);
    }


    public boolean proximo() throws Exception {
        if (!aberto) {
            executar();
            aberto = true;
        }
        return resultSet.next();
    }

    public boolean loop() throws Exception {
        return proximo();
    }


    public NativeSqlDecorator(Object objetobase, String arquivo) throws Exception {
        iniciar();

        //nativeSql.appendSql(getSqlResource(objetobase, arquivo));
        nativeSql.loadSql(objetobase.getClass(), arquivo);
    }

    public NativeSqlDecorator setParametro(String nome, Object valor) {
        nativeSql.setNamedParameter(nome, valor);
        return this;
    }

    public BigDecimal getValorBigDecimal(String campo) throws Exception {
        return resultSet.getBigDecimal(campo);
    }

    public String getValorString(String campo) throws Exception {
        return resultSet.getString(campo);
    }

    private Boolean getValorBoolean(String campo) throws Exception {
        return resultSet.getBoolean(campo);
    }

    public Timestamp getValorTimestamp(String campo) throws Exception {
        return resultSet.getTimestamp(campo);
    }

    public int getValorInt(String campo) throws Exception {
        return resultSet.getInt(campo);
    }

    private float getValorFloat(String campo) throws Exception {
        return resultSet.getFloat(campo);
    }


    private void iniciar() {
        nativeSql = new NativeSql(EntityFacadeFactory.getDWFFacade().getJdbcWrapper());
    }

    private void executar() throws Exception {
        resultSet = nativeSql.executeQuery();
        if (resultSet != null) {
            aberto = true;
        }
    }

    public void atualizar() throws Exception {
        nativeSql.executeUpdate();

    }

    @Override
    public void run() {
        try {
            atualizar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getSqlResource(Object objetobase, String arquivo) throws Exception {
        InputStream in = objetobase.getClass().getResourceAsStream(arquivo);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuffer buf = new StringBuffer(512);
        String line = null;

        while ((line = reader.readLine()) != null) {
            buf.append(line);
            buf.append('\n');
        }

        return buf.toString();
    }
    public void acrescentarSql(String sql) {
        nativeSql.appendSql(sql);
    }

    public void removeComentarioSQL(String nomeComentario){
        nativeSql.removeSQLComment(nomeComentario);
    }

    public void substituirComentarioSQL(String nomeComentario,String  valor){
        nativeSql.replaceSQLComment(nomeComentario,valor);
    }
}

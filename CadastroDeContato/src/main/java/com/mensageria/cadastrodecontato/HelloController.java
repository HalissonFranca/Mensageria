package com.mensageria.cadastrodecontato;

import com.mensageria.cadastrodecontato.dao.MensagemDao;
import com.mensageria.cadastrodecontato.model.Mensage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Optional;

public class HelloController {

    @FXML
    private Button BtExcluir;

    @FXML
    private Button BtListar;

    @FXML
    private Button BtSalvar;

    @FXML
    private Label LbErro;

    @FXML
    private TextField TxtEmail;

    @FXML
    private TextField TxtNome;

    @FXML
    private TextField TxtTelefone;

    @FXML
    private TextField txtId;

    @FXML
    void onHelloButtonEcluir(ActionEvent event){
        //excluir pelo ID

        Long valor = Long.parseLong(txtId.getText());

        MensagemDao dao = new MensagemDao();

        Optional<Mensage> Omsg = dao.read(valor);
        Mensage msg = Omsg.orElse(null);

        if (msg != null) {
            TxtNome.setText(msg.getNome());
            TxtTelefone.setText(msg.getTelefone());
            TxtEmail.setText(msg.getEmail());
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação de Exclusão");
        alert.setHeaderText("Você tem certeza que deseja excluir este registro?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            dao.delete(valor);

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Sucesso");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Registro excluído com sucesso!");
            successAlert.showAndWait();

            TxtNome.clear();
            TxtTelefone.clear();
            TxtEmail.clear();
        } else {

            Alert cancelAlert = new Alert(Alert.AlertType.INFORMATION);
            cancelAlert.setTitle("Cancelado");
            cancelAlert.setHeaderText(null);
            cancelAlert.setContentText("Exclusão cancelada.");
            cancelAlert.showAndWait();
        }
    }

    @FXML
    void onHelloButtonlistar(ActionEvent event) {
        //listar Todos
        MensagemDao dao = new MensagemDao();
        List<Mensage> mensagens = dao.readAll();

        StringBuilder registros = new StringBuilder();
        for (Mensage msg : mensagens) {
            registros.append("ID: ").append(msg.getId())
                    .append(" - Nome: ").append(msg.getNome())
                    .append(" - Email: ").append(msg.getEmail())
                    .append(" - Telefone: ").append(msg.getTelefone())
                    .append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Listagem de Registros");
        alert.setHeaderText("Todos os Registros Salvos");
        alert.setContentText(registros.toString());
        alert.showAndWait();
    }


    @FXML
    void onHelloButtonSalvar(ActionEvent event) {
            //botaode salvar
            String texto = TxtNome.getText() + " " + TxtTelefone.getText();

            MensagemDao dao = new MensagemDao();
            Mensage msg = new Mensage();

            msg.setNome(TxtNome.getText());
            msg.setTelefone(TxtTelefone.getText());
            msg.setEmail(TxtEmail.getText());


        if (TxtNome.getText().isEmpty() || TxtTelefone.getText().isEmpty() || TxtEmail.getText().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Preencha todos os campos!");
            alert.showAndWait();
        } else {

            dao.create(msg);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Registro salvo com sucesso!");
            alert.showAndWait();

            TxtEmail.clear();
            TxtNome.clear();
            TxtTelefone.clear();
        }

    }
}

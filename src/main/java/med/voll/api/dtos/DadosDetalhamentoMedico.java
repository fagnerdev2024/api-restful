package med.voll.api.dtos;

import med.voll.api.entities.Endereco;
import med.voll.api.enums.Especialidade;
import med.voll.api.entities.Medico;

public record DadosDetalhamentoMedico(

        Long id,
        String nome,
        String email,
        String crm,
        String telefone,
        Especialidade especialidade,
        Endereco endereco) {

    public DadosDetalhamentoMedico (Medico medico) {
        this(medico.getId(),
            medico.getNome(),
            medico.getEmail(),
            medico.getCrm(),
            medico.getTelefone(),
            medico.getEspecialidade(),
            medico.getEndereco());

    }




    @Override
    public Long id() {
        return id;
    }

    @Override
    public String nome() {
        return nome;
    }

    @Override
    public String email() {
        return email;
    }

    @Override
    public String crm() {
        return crm;
    }

    @Override
    public String telefone() {
        return telefone;
    }

    @Override
    public Especialidade especialidade() {
        return especialidade;
    }

    @Override
    public Endereco endereco() {
        return endereco;
    }
}

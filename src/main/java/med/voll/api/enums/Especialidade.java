package med.voll.api.enums;

public enum Especialidade {

    ORTOPEDIA("Tratamento de condições do sistema musculoesquelético, incluindo ossos, músculos e articulações."),
    CARDIOLOGIA("Diagnóstico e tratamento de doenças do coração e do sistema cardiovascular."),
    GINECOLOGIA("Cuidados médicos relacionados à saúde feminina, incluindo o sistema reprodutivo."),
    DERMATOLOGIA("Estudo e tratamento de condições relacionadas à pele, cabelos e unhas."),
    NEUROLOGIA("Diagnóstico e tratamento de distúrbios do sistema nervoso, incluindo cérebro e medula espinhal."),
    PEDIATRIA("Cuidados médicos voltados para crianças e adolescentes, abrangendo crescimento e desenvolvimento.");

    private final String descricao;

    Especialidade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return name() + " - " + descricao;
    }
}
package med.voll.api.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RepositoryFacade {


    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;


    public ConsultaRepository getConsultaRepository() {
        return consultaRepository;
    }

    public MedicoRepository getMedicoRepository() {
        return medicoRepository;
    }

    public PacienteRepository getPacienteRepository() {
        return pacienteRepository;
    }
}

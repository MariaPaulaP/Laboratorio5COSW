package edu.eci.cosw.samples;


import edu.eci.cosw.example.persistence.PatientsRepository;
import edu.eci.cosw.jpa.sample.model.Consulta;
import edu.eci.cosw.jpa.sample.model.Paciente;
import edu.eci.cosw.jpa.sample.model.PacienteId;
import edu.eci.cosw.samples.services.PatientServices;
import edu.eci.cosw.samples.services.ServicesException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringDataRestApiApplication.class)
@WebAppConfiguration

@ActiveProfiles("test")
public class SpringDataRestApiApplicationTests {

        
@Autowired
    private PatientsRepository patientrepo;

    @Autowired
    private PatientServices patientserv;

    @Test
	public void existeUnPaciente(){

        PacienteId pId = new PacienteId(2095957, "cc");
        Paciente paciente = new Paciente(pId, "Paula Pinzon", new Date());
        patientrepo.save(paciente);
        
        try {
            Paciente pacienteConsulta = patientserv.getPatient(2095957, "cc");
            Assert.assertEquals(pacienteConsulta.getId().getId()+pacienteConsulta.getId().getTipoId(),paciente.getId().getId()+paciente.getId().getTipoId());
        } catch (ServicesException ex) {
            Logger.getLogger(SpringDataRestApiApplicationTests.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail("Existe el paciente");
        }
    }

    @Test
    public void noExistePaciente(){

        patientrepo.deleteAll();
        PacienteId pid=new PacienteId(2095957,"cc");
        Paciente paciente = new Paciente(pid, "Paula Pinzon", new Date(1990,01,01));
        Paciente pacientePru=patientrepo.findOne(pid);
        Assert.assertNull("No existe",pacientePru);
    }

    @Test
    public void noExistePacientesConNConsultas() throws ServicesException {

        patientrepo.deleteAll();
        PacienteId pacienteId=new PacienteId(2095957,"cc");
        Paciente paciente = new Paciente(pacienteId, "Paula Pinzon", new Date(2000,10,05));
        paciente.getConsultas().add(new Consulta(new Date(2012,5,21), "Pereza"));
        patientrepo.save(paciente);
        List<Paciente> pacientes = patientserv.topPatients(2);
        Assert.assertEquals("Lista vacia",pacientes.size(),0);

    }

    @Test
    public void existePacientesConNConsultas() throws ServicesException {

        patientrepo.deleteAll();
        PacienteId pacienteid=new PacienteId(2095957,"cc");
        PacienteId pacienteid2=new PacienteId(123456,"cc");
        PacienteId pacienteid3=new PacienteId(9876541,"cc");
        
        Paciente paciente = new Paciente(pacienteid, "Paula Pinzon", new Date(1990,01,01));       
        Paciente paciente2 = new Paciente(pacienteid2, "Pepito perez", new Date(2010,12,12));        
        Paciente paciente3 = new Paciente(pacienteid3, "Florencio perez", new Date(2001,11,11));

        patientrepo.save(paciente);
        
        
        paciente2.getConsultas().add(new Consulta(new Date(2010,15,20), "Dolor"));
        patientrepo.save(paciente2);

        paciente3.getConsultas().add(new Consulta(new Date(2000,5,16), "Cabeza"));
        paciente3.getConsultas().add(new Consulta(new Date(2005,19,17), "Gripa"));
        patientrepo.save(paciente3);

        List<Paciente> pacientes = patientserv.topPatients(1);

        Assert.assertEquals("Lista",pacientes.size(),2);
    }

}

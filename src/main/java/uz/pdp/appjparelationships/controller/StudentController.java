package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;

import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    GroupRepository groupRepository;

//CRUD
    @PostMapping
    public String addStudent(@RequestBody StudentDto studentDto ){
        Optional<Address> optionalAddress = addressRepository.findById(studentDto.getAddress_id());
        if (!optionalAddress.isPresent()) return "Adress not found";

        Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroup_id());
        if (!optionalGroup.isPresent()) return "Group not found";

        Student student=new Student(null,studentDto.getFirstName(),studentDto.getLastName(),optionalAddress.get(),optionalGroup.get(),studentDto.getSubjects());

        return "Student saved";
    }
    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/getStudentListForUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    @GetMapping("/getStudentListForDekanat/{facultyId}")
    public Page<Student> getStudentListForDekanat(@PathVariable Integer dekanatId,
                                                  @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 3);
        Page<Student> studentPage = studentRepository.findAllByGroup_FacultyId(dekanatId, pageable);
        return studentPage;
    }

    //4. GROUP OWNER
    @GetMapping("/getStudentListForGroupLider/{groupId}")
    public Page<Student> getStudentListForGroupLider(@PathVariable Integer groupId,
                                                     @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 3);
        Page<Student> studentPage = studentRepository.findAllByGroup_Id(groupId, pageable);
        return studentPage;
    }

    @PutMapping("/{studentId}")
    public String editStudent(@PathVariable Integer studentId, @RequestBody StudentDto studentDto){
        Optional<Student> optionalStudent = studentRepository.findById(studentId);
        if (!optionalStudent.isPresent())return "Student not found";

        Student student = optionalStudent.get();
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());

        Optional<Address> optionalAddress = addressRepository.findById(studentDto.getAddress_id());
        if (!optionalAddress.isPresent()) return "Address not found";
        student.setAddress(optionalAddress.get());

        Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroup_id());
        if (!optionalGroup.isPresent()) return  "Group not found";
        student.setGroup(optionalGroup.get());

        student.setSubjects(studentDto.getSubjects());
        studentRepository.save(student);
        return "Student saved";

    }

    @DeleteMapping("/{studentId}")
    public String deleteStudent(@PathVariable Integer studentId){
        studentRepository.deleteById(studentId);
        return "Student deleted";
    }

}

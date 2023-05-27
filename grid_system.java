import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class GridSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(GridSystemApplication.class, args);
    }

    @RestController
    public class StudentController {

        private List<Student> students;

        public StudentController() {
            students = loadStudentDataFromFile(); // Load student data from a file
        }

        @GetMapping("/students")
        public List<Student> getStudents(
                @RequestParam(required = false, defaultValue = "1") int page,
                @RequestParam(required = false, defaultValue = "10") int pageSize,
                @RequestParam(required = false) Integer id,
                @RequestParam(required = false) String name,
                @RequestParam(required = false) Integer totalMarks
        ) {
            List<Student> filteredStudents = students;

            // Apply filters if provided
            if (id != null) {
                filteredStudents = filteredStudents.stream()
                        .filter(student -> student.getId() == id)
                        .collect(Collectors.toList());
            }

            if (name != null) {
                filteredStudents = filteredStudents.stream()
                        .filter(student -> student.getName().toLowerCase().contains(name.toLowerCase()))
                        .collect(Collectors.toList());
            }

            if (totalMarks != null) {
                filteredStudents = filteredStudents.stream()
                        .filter(student -> student.getTotalMarks() == totalMarks)
                        .collect(Collectors.toList());
            }

            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, filteredStudents.size());

            if (startIndex >= filteredStudents.size()) {
                return new ArrayList<>();
            }

            return filteredStudents.subList(startIndex, endIndex);
        }

        private List<Student> loadStudentDataFromFile() {
            try {
                ClassPathResource resource = new ClassPathResource("student_data.json"); // Replace with the file path
                String jsonData = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

                ObjectMapper objectMapper = new ObjectMapper();
                List<Student> students = objectMapper.readValue(jsonData, objectMapper.getTypeFactory().constructCollectionType(List.class, Student.class));

                return students;
            } catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
    }

    public static class Student {
        private int id;
        private String name;
        private int totalMarks;

        // Constructors, getters, and setters

        public Student() {
        }

        public Student(int id, String name, int totalMarks) {
            this.id = id;
            this.name = name;
            this.totalMarks = totalMarks;
        }

        // Getters and Setters
    }
}

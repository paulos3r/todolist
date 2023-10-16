package br.paulos3r.todolist.task;

import br.paulos3r.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;


    @PostMapping("/")
    public ResponseEntity created(@RequestBody TaskModel taskModel, HttpServletRequest request){

      var id_user = request.getAttribute("id_user");

      taskModel.setId_user((UUID) id_user);


      var currentDate = LocalDateTime.now();

      if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt()) ){
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio ou a data de termino deve ser maior do que a data atual ");
      }
      if (taskModel.getStartAt().isAfter(taskModel.getEndAt()) ){
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body("a data de inicio deve ser menor que a data de termino");
      }
      var task = this.taskRepository.save(taskModel);

      return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){
      var id_user = request.getAttribute("id_user");

      var tasks = this.taskRepository.findByIdUser((UUID) id_user);

      return tasks;
    }

    @PutMapping("/{id}")
    public TaskModel update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request){
      var id_user = request.getAttribute("id_user");

      var task = this.taskRepository.findById(id).orElse(null);

      Utils.copyNonNullProperties(taskModel, task);

      return this.taskRepository.save(task);
    }
}
package cn.iocoder.springboot.lab27.springwebflux.controller;

import cn.iocoder.springboot.lab27.springwebflux.dto.UserAddDTO;
import cn.iocoder.springboot.lab27.springwebflux.dto.UserUpdateDTO;
import cn.iocoder.springboot.lab27.springwebflux.service.UserService;
import cn.iocoder.springboot.lab27.springwebflux.vo.UserVO;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户 Controller
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 查询用户列表
     *
     * @return 用户列表
     */
    @GetMapping("/list")
    public Flux<UserVO> list() {
        // 查询列表
        List<UserVO> result = new ArrayList<>();
        result.add(new UserVO().setId(1).setUsername("yudaoyuanma"));
        result.add(new UserVO().setId(2).setUsername("woshiyutou"));
        result.add(new UserVO().setId(3).setUsername("chifanshuijiao"));
        // 返回列表
        return Flux.fromIterable(result);
    }

    /**
     * 获得指定用户编号的用户
     *
     * @param id 用户编号
     * @return 用户
     */
    @GetMapping("/get")
    public Mono<UserVO> get(@RequestParam("id") Integer id) {
        // 查询用户
        UserVO user = new UserVO().setId(id).setUsername("username:" + id);
        // 返回
        return Mono.just(user);
    }

    /**
     * 获得指定用户编号的用户
     *
     * @param id 用户编号
     * @return 用户
     */
    @GetMapping("/v2/get")
    public Mono<UserVO> get2(@RequestParam("id") Integer id) {
        // 查询用户
        UserVO user = userService.get(id);
        // 返回
        return Mono.just(user);
    }

    /**
     * 添加用户
     *
     * @param addDTO 添加用户信息 DTO
     * @return 添加成功的用户编号
     */
    @PostMapping("add")
    public Mono<Integer> add(@RequestBody Publisher<UserAddDTO> addDTO) {
        // 插入用户记录，返回编号
        Integer returnId = 1;
        // 返回用户编号
        return Mono.just(returnId);
    }

    @PostMapping("add3")
    public Mono<Integer> add3(@RequestBody UserAddDTO addDTO) {
        System.out.println("add3 addDTO:" + addDTO);
        // 插入用户记录，返回编号
        Integer returnId = 1;
        // 返回用户编号
        return Mono.just(returnId);
    }

    @PostMapping("add4")
    public Mono<Integer> add4(@RequestParam("username") String username, @RequestParam("password") String password) {
        System.out.printf("add4: username='%s',password='%s'\n", username, password);
        // 插入用户记录，返回编号
        Integer returnId = 1;
        // 返回用户编号
        return Mono.just(returnId);
    }

    //@ModelAttribute("user")
    //public UserAddDTO getUserModel() {
    //    UserAddDTO userAddDTO = new UserAddDTO();
    //    userAddDTO.setUsername("kwseeker");
    //    userAddDTO.setPassword("3333");
    //    return userAddDTO;
    //}

    //@InitBinder对@RequestBody这种基于消息转换器的请求参数无效
    //因为@InitBinder它用于初始化DataBinder数据绑定、类型转换等功能，而@RequestBody它的数据解析、转换时消息转换器来完成的，所以即使你自定义了属性编辑器，对它是不生效的
    //@InitBinder("user")
    //@InitBinder({"username", "password", "user"})
    @InitBinder({"username", "user"})
    public void initBinder(WebDataBinder binder) {
        System.out.println("initBinder");
        binder.addValidators(new UserValidator());
        //binder.addValidators(new StringParamValidator());
        binder.registerCustomEditor(String.class, new StringTrimmerEditor());
    }

    /**
     * 添加用户
     *
     * @param addDTO 添加用户信息 DTO
     * @return 添加成功的用户编号
     */
    @PostMapping("add2")
    public Mono<Integer> add2(Mono<UserAddDTO> addDTO) {
        // 插入用户记录，返回编号
        Integer returnId = 1;
        // 返回用户编号
        return Mono.just(returnId);
    }

    /**
     * 更新指定用户编号的用户
     *
     * @param updateDTO 更新用户信息 DTO
     * @return 是否修改成功
     */
    @PostMapping("/update")
    public Mono<Boolean> update(@RequestBody Publisher<UserUpdateDTO> updateDTO) {
        // 更新用户记录
        Boolean success = true;
        // 返回更新是否成功
        return Mono.just(success);
    }

    /**
     * 删除指定用户编号的用户
     *
     * @param id 用户编号
     * @return 是否删除成功
     */
    @PostMapping("/delete") // URL 修改成 /delete ，RequestMethod 改成 DELETE
    public Mono<Boolean> delete(@RequestParam("id") Integer id) {
        // 删除用户记录
        Boolean success = true;
        // 返回是否更新成功
        return Mono.just(success);
    }

}

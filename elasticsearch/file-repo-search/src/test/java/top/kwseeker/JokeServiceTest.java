package top.kwseeker;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import top.kwseeker.po.Joke;
import top.kwseeker.service.JokeService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JokeServiceTest {

    @Resource
    private JokeService jokeService;

    @Test
    @Order(1)
    public void testSaveJokes() {
        jokeService.deleteJokes();

        List<Joke> jokes = new ArrayList<>();
        jokes.add(new Joke("段子1",
                "一日K歌回来，老公喝醉了，听说醉后都会说实话。 于是问道：“以后有钱了干嘛?”老公：“要娶五个老婆！” 我怒了：“为什么不学韦小宝娶七个回来？” 老公恍惚道：“太累，我需要双休！",
                23));
        jokes.add(new Joke("段子2",
                "一天丈夫下班回家对妻子说，今天中午吃饭的时候一个算命先生对我说，我125岁的时候会有一大灾，没想到妻子回了句，怎么了，你的坟被人刨了。",
                82));
        jokes.add(new Joke("段子3",
                "一贵妇独自在珠宝店买首饰，付款时门口进来一男的二话不说上去就给那女的一耳光，“TMD，不让你买你听不懂啊！”，然后夺过那女人手里的包满面怒容的离开了，当时那女的被一耳光打傻了，过了一会才反应过来，大叫：“抢劫啊……”，收银员也愣了：“那不是你老公吗？”。",
                11));
        jokes.add(new Joke("段子4",
                "一天，小明老师去小明家家访，小明爸爸连忙开门，小明老师做自我介绍说:我姓金，叫金莲。小明爸爸连忙说:潘老师快请进！",
                9));
        jokes.add(new Joke("段子5",
                "一天弟弟上厕所忘记带手纸,就叫哥哥“给我拿点手纸来”哥哥在玩电脑说“等一会”，结果哥哥在玩游戏，半小时后哥哥想起来，去给弟弟送手纸。打开厕所门看见弟弟提裤子问“不要了？”弟弟说“不用了，已经干了”！",
                13));
        jokes.add(new Joke("段子6",
                "乌龟受伤.让蜗牛去买药。过了2个小时.蜗牛还没回来。乌龟急了骂道:草你妈的再不回来老子就死了!这时门外传来了蜗牛的声音:你他妈的再说老子不去了",
                99));
        jokes.add(new Joke("段子7",
                "儿童节一到，怀着感恩之心的我，决定在这个特殊日子给我接触过的每位女老师都写上一封信，信中感谢她们一直以来对我的照顾，感谢她们对我特殊的体贴和关心，告诉她们我一直爱她们！ 结果信一寄出就收到了良好的反响，50多岁的教导主任亲自找到我说：我当老师30多年了，就没见过你这样不要脸的校长！",
                36));
        jokes.add(new Joke("段子8",
                "本人姓袁，老婆这个月底生，给孩子取名，想了很多名字，今天刚躺床上， 老婆说:子轩这个名字好听吗?当时我突然接了句不如叫袁子弹吧，小名叫嘣!!!当时老婆正在吃橘子，结果直接喷了我一脸",
                28));
        jokes.add(new Joke("段子9",
                "昨天火车上上厕所，好多人排队，终于到我了，从里面出来了一个挺漂亮的妹子，出来后甩了甩手，甩到我脸上一滴水，当时没在意，关键我上完厕所愣是没找到水管子。",
                43));
        jokes.add(new Joke("段子10",
                "老婆是个女汉子，有一天下班风风火火回到家进门就大喊：“老公我回来了，今天公交车上遇到小偷，从我身边一过我就发现我手机没了，哈哈！于是我下了公交我追他两站啊，逮住之后一顿打，但他死活不承认翻遍他全身都没找到我的诺基亚，小偷哭着求我说，大姐要不然这些手机你随便挑一个吧于是我就拿了个苹果6回来。”我惊愕的说道：“老婆，你今天没带手机啊，这诺基亚不在茶几上吗 ……",
                67));
        jokes.add(new Joke("段子11",
                "媳妇儿没事玩老公手机不小心摔了！手机壳电池都摔出来了！还有藏在里面的100块钱！媳妇儿看着老公说：解释一下吧！老公恐慌的说：我的天呐::::话费都他妈地摔出来啦！…",
                64));
        jokes.add(new Joke("段子12",
                "今天早上上班带了个芒果吃，上公交车后就一直再玩手机，便顺手把芒果塞屁兜里了。当我坐下的时候，一声轻轻的闷响，一大摊黄色的芒果肉就从屁股下面挤了出来，然后….然后一车人都下去了，我勒个去，叔叔阿姨们咋都不听我解释啊！为了解释清楚，我抓了一把放嘴里，结果下去那一车人全吐了。",
                55));
        jokes.add(new Joke("段子13",
                "毕业后，我到一所女子学校任教，由于年轻的男老师极少，所以我总以为自己这个纯爷们儿将非常受欢迎。第一堂课，有一位女学生忘记带课本，我请她站起来，问其他同学该怎么处罚。她们很有默契的齐声说：“老师亲她一个！”大家等着看我的反应。我瞄了那位同学一眼，只好回答：“不可以处罚老师！”",
                96));
        jokes.add(new Joke("段子14",
                "四个人打麻将，突然着火了，他们都没有注意到。消防员赶到了，冲里面大喊道：里面有多少人?这时，刚好有一个人出牌：四万!消防员又问：死了多少人?这时，又有一个人出牌：两万!消防员大惊，慌忙问道：剩下的人呢?只听哗啦一声，紧接着，传了一声尖叫：糊了。",
                64));
        jokes.add(new Joke("段子15",
                "某人上课睡觉，老师见了火大，就叫他到黑板上解题，准备当众羞辱他。才站起来，老师就开始酸他：“成绩那么差，还敢上课睡觉，真不知羞耻，就会睡觉...”结果某人漂亮的把题解出来了。老师顿时有点下不来台。结果他自己走回座位，坐下淡淡的说：“我先睡一下，你待会还有不会的再问我。”",
                82));
        jokes.add(new Joke("段子16",
                "刘大爷一心想要个孙子，哪知道儿子不争气生个老大是女儿。刘大爷那个郁闷啊!于是取名叫招弟;哪知道老二又是女儿、取名再招;老三依旧是女儿、取名还招;老四仍然是个女儿，刘大爷跪在祖宗牌位前哭得昏天暗地，完事给老四取名——绝招………",
                38));
        jokes.add(new Joke("段子17",
                "一天，动物园的一只大象突然死去，饲养员赶来立即伏在大象身上痛哭起来。游客们见此情景，不由深受感动，纷纷说：“这位饲养员和这只大象的感情太深了。”不料有一人插话道：“这个动物园有个规定，如果谁饲养的动物死了，那么这个动物的墓穴就得由那个饲养员去挖，他怎能不哭呢?”",
                76));
        jokes.add(new Joke("段子18",
                "小明：我要请假。 老师：理由。 小明：我下午要做手术。 老师：什么手术。 小明：人体无用副组织群体切除术。 老师：说人话。 小明：理发。 老师：滚出去!",
                45));
        jokes.add(new Joke("段子19",
                "今天第一次带女友回老家，女友长的漂亮人还勤快，见老爸要抽烟，女友忙拿起火机给老爸点上，又是给老爸沏茶又是倒茶……老爸一高兴拿出1000块钱红包，说初次见面给个见面礼，女友接过钱说了声谢谢老板，老板，板…",
                36));
        jokes.add(new Joke("段子20",
                "无意中看到段子上说女人冬天都不戴胸罩，无比纳闷便问女友：你们女人冬天是不是都不戴胸罩？女友二话没说啪啪两巴掌问道：你见哪个女人没戴胸罩？答：段子，女友又两巴掌说道，你还长出息了！还找了个日本娘们！",
                76));
        jokes.add(new Joke("段子21",
                "我的男神以前跟我说过他喜欢爱笑的女孩子，因为爱笑的女孩子看起来很漂亮，从此以后我在他面前表现得特爱笑。前阵子他谈女友了，当然不是我，他女友是脸沉沉，很少笑的那一款，我特地问了下男神，你不是说你喜欢爱笑的女孩子吗，说这样子比较漂亮，这时男神回复我：你不觉得，她不笑也很漂亮吗？",
                95));
        jokes.add(new Joke("段子22",
                "看到门口有两个五岁左右的小孩在下象棋，我看了一眼说：“这小男孩可能还会下，小女孩根本瞎走，象哪儿能走到那儿！”邻居看了我一眼叹气道：“人家五岁就懂的道理，你二十五岁还不懂，知道你为啥单身了吧……”",
                83));
        jokes.add(new Joke("段子23",
                "老俩口看电视，突然转播选美比赛。老头子一看，脸红了，转身进屋。老太太笑：老头子还挺封建。一会老头子回来了，端正坐好，脸上多了副老花眼镜……",
                92));
        jokes.add(new Joke("段子24",
                "就在刚才，我突然发现我家的狗小黑是个狗才，我正准备坐下来吃麦当劳买回来的汉堡，小黑忽然对着窗户外面大叫一声，好像陌生人进了院子似的，我出去看了一圈，没见到人，再回来的时候，发现小黑和汉堡都不见了.....",
                80));
        jokes.add(new Joke("段子25",
                "上大学时追一女孩，数次表白，无果。后来女孩短信约我周末去公园，我激动得晚上没睡好觉。周末应邀到黄河公园。走了一阵，女孩说：“我有句话一直想对你说…”我那个激动啊，心想这事有戏，就说“你说吧，我听着。”然后她告诉我：“黄河也看过了，这回死心了吧？！！”",
                52));
        jokeService.saveJokes(jokes);
    }

    @Test
    @Order(3)
    public void testDeleteJokes() {
        jokeService.deleteJokes();
    }

    @Test
    @Order(2)
    public void testFindJokes() {
        //curl -X POST http://localhost:9200/_analyze \
        //  -H 'content-type: application/json' \
        //  -d '{
        //        "analyzer": "ik_max_word",
        //        "text": "段子1"
        //}'
        //{"tokens":[{"token":"段子","start_offset":0,"end_offset":2,"type":"CN_WORD","position":0},{"token":"1","start_offset":2,"end_offset":3,"type":"ARABIC","position":1}]}%
        List<Joke> jokes;
        jokes = jokeService.findByTitle("段子");
        assertEquals(25, jokes.size());

        // 生成其他 Find 接口的测试
        jokes = jokeService.findByTitleCustom("段子");
        assertEquals(25, jokes.size());

        Page<Joke> jokesPage = jokeService.findByTitle("段子", PageRequest.of(0, 10, Sort.by("likeCount").descending()));
        assertEquals(10, jokesPage.getContent().size());

        jokes = jokeService.findByContent("老师");
        assertEquals(5, jokes.size());
        jokes = jokeService.findByContent("电视");
        assertEquals(1, jokes.size());
        jokes = jokeService.findByContent("老师看电视");
        assertEquals(0, jokes.size());
        jokes = jokeService.findByContentFuzzy("老师看电视");
        assertEquals(5 + 1, jokes.size());

        jokes = jokeService.findByTitleAndContent("段子", "乌龟");
        assertEquals(1, jokes.size());

        jokes = jokeService.findByLikeCount(99);
        assertEquals(1, jokes.size());

        jokes = jokeService.findByLikeCountBetween(50, 100);
        assertEquals(15, jokes.size());

        jokes = jokeService.findByLikeCountGreaterThan(80);
        assertEquals(7, jokes.size());

        jokes = jokeService.findByLikeCountGreaterThanEqual(80);
        assertEquals(8, jokes.size());

        jokes = jokeService.findByLikeCountLessThan(20);
        assertEquals(3, jokes.size());
    }
}

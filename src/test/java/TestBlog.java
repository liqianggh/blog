//import org.junit.Test;

/*
 * 无处不再的跳转
 * alt + num 展开指定窗口
 * <p>
 * 项目于之间跳转alt +ctrl +[/]
 * 查看快捷键ctrl shift + a
 * 查看最近文件ctrl+e
 * esc 跳转到代码编辑页面
 * 最近编辑文件 ctrl +shift +e
 * <p>
 * 上一次修改焦点ctrl+q
 * 上一次浏览焦点 alt+ 左右箭头
 * <p>
 * 代码添加书签：ctrl alt shift +f11 选择一个编号
 * ctrl+编号  跳转到指定书签
 * <p>
 * alt + shfit +f 收藏整个类
 * 选择函数  alt+shift +f 收藏函数
 * <p>
 * 安装emacsideas后  ctrl+j+“字母””  选中指定字母跳转
 * <p>
 * alt+1跳转到左右区域
 * <p>
 * 精准搜索
 * ctrl+shift +t （再按一次可搜索jar里面的class）
 * ctrl+shift +r搜索文件
 * ctrl+shift+n符号搜索 如方法 ,字段
 * ctrl+h 精准搜索
 * <p>
 * 列批量操作操作
 * ctrl + shift +u 大小写转换
 * ctrl+右键头/左箭头选中一个单词
 * ctrl+alt+y选中所有同类型
 * <p>
 * 代码小组守live template
 * 设置注解
 * 1构造函数里传入没有的字段 在构造函数中输入 字段.filed即可生成
 * 方法返回值中  ziduan.return 生成return ziduan;
 * 字段.nn if(user!=null){}
 * <p>
 * alt+enter代码智能提示
 * 自动创建函数method([args])
 * 光标放在for循环旁边自动替换成while和foreach
 * 格式化输出
 * 创建本接口或类的子类
 * 单词拼写改正
 * <p>
 * 代码重构
 * 重构变量alt shift +r 修改当前变量名
 * 重构方法alt  shift +c
 * 抽取
 * alt ctrl +c 抽取静态量常量
 * alt+ctrl +v 抽取为字段
 * alt ctrl f 抽取为成员变量
 * alt ctrl +p 抽取为方法参数
 * alt +shift +m 抽取为方法
 *
 * ctrl+shift+f10运行当前上下文函数
 *
 * 在当前列表中选择一个可运行函数 shift alt +f10
 *
 * 其他操作
 * 文件操作 ctrl+shift +n 创建新的文件
 * 复制完整文件名——对着文件shift+Ctrl+C。
 * 复制文件名——对着文件Ctrl+C。
 *批量复制——调用剪切板，shift+Ctrl+V。
 *
 *ctrl+f3 查看类的方法结构
 * ctrl shift alt +u
 * ctrl+alt +h 查看函数调用情况
 */
public class TestBlog {

//    @Test
    public void test() {

        for(int i = 0;i<=100;i++){
            i+=i;
            System.out.println(i);
        }

    }
public static void main(String [] args){
    System.out.println(args[0]); }
}

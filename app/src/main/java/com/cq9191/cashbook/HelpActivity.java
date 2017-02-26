package com.cq9191.cashbook;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class HelpActivity extends Activity {

    private final String help = "1、添加新账单：\n\n" +
            "(1)点击界面中右下角按钮，即可进入添加账单界面\n" +
            "(2)手动输入或选择人物、事件卡(可滑动，可编辑)，手动输入账单金额和描述\n" +
            "(3)点击右上角完成按钮，添加新账单成功\n" +
            "(4)点击左上角返回按钮，取消本次操作\n" +
            "  \n" +
            "  \n" +
            "  \n" +
            "2、修改账单：\n\n" +
            "(1)登录超级用户后，点击想要修改的账单\n" +
            "(2)进入账单详情页后，点击修改账单按钮\n" +
            "(3)进入账单编辑页，修改本账单相关信息\n" +
            "(4)点击完成修改按钮，即修改成功\n" +
            "  \n" +
            "  \n" +
            "  \n" +
            "3、删除存储的账单：\n\n" +
            "(1)登录超级用户后，点击自定义清除账单\n" +
            "(2)选择您想要删除的账单特点\n" +
            "(3)点击确定按钮，可选择“直接删除”和“预览”将要删除的账单\n" +
            "(4)预览后，点击确认删除，即可删除数据\n" +
            "  \n" +
            "  \n" +
            "  \n" +
            "4、用户注册登录：\n\n" +
            "(1)在首页面点击右上角home按钮，进入home界面\n" +
            "(2)新用户点击注册按钮，注册超级用户\n" +
            "(3)输入超级用户账号、密码，登录\n" +
            "(4)登录后，在个人中心定制适合自己的记账本\n" +
            "  \n" +
            "  \n" +
            "  \n" +
            "5、个人中心：\n\n" +
            "(1)修改超级用户登录密码\n" +
            "(2)可添加、删除人物卡，在添加新账单时可直接选择\n" +
            "(3)可添加、删除事件卡，在添加新账单时可直接选择\n" +
            "(4)生成自定义的总账单，便于统计、整理数据\n" +
            "(5)自定义筛选条件，删除一批账单\n" +
            "(6)意见反馈，提交您的宝贵意见\n" +
            "(7)关于我们，了解开发者信息\n" +
            "  \n" +
            "  \n" +
            "  \n" +
            "5、超级用户说明：\n\n" +
            "(1)每1个APP仅有1个超级用户(即只能注册1次)\n" +
            "(2)仅有超级用户可以修改、删除账单记录\n" +
            "(3)仅有超级用户可以编辑人物、事件卡\n" +
            "(4)仅有超级用户可以生成自定义总账单\n"+
            "  \n" +
            "  \n" +
            "  \n" +
            "6、注意事项：\n\n" +
            "(1)修改账单时，注意修改格式，避免查询结果不准确\n" +
            "   年月日格式为：2000-01-01\n" +
            "   时间格式为：01:01\n" +
            "   星期格式为：周x\n" +
            "(2)删除账单数据后，数据不可恢复，请谨慎删除账单\n" +
            "(3)卸载该App后，存储的账单数据将全部删除\n";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_layout);

        TextView textView = (TextView) findViewById(R.id.help_text);
        textView.setText(help);

        final ImageButton close = (ImageButton) findViewById(R.id.bt_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HelpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

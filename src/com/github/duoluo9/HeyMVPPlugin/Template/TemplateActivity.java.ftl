package $packagename;

import com.sskj.common.base.BaseActivity;
import $packagename.$namePresenter;
import android.content.Context;
import android.content.Intent;
import  $rPackageName.R;
/**
 * @author $author
 * Create at  $date
 */
public class $nameActivity extends BaseActivity<$namePresenter> {



    @Override
    public int getLayoutId() {
        return R.layout.$layout;
    }

    @Override
    public $namePresenter getPresenter() {
        return new $namePresenter();
    }

    @Override
    public void initView() {

       }

    @Override
    public void initData() {

    }

    public static void start(Context context){
        Intent intent=new Intent(context,$nameActivity.class);
        context.startActivity(intent);
    }

}

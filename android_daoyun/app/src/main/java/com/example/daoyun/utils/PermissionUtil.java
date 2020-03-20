package com.example.daoyun.utils;

public class PermissionUtil {

//    public static boolean requestPermissions(final Context context,String permission,int requestTag) {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return true;
//        }
//        if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        if (context.shouldShowRequestPermissionRationale(READ_CONTACTS)) {
//            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
//                    .setAction(android.R.string.ok, new View.OnClickListener() {
//                        @Override
//                        @TargetApi(Build.VERSION_CODES.M)
//                        public void onClick(View v) {
//                            context.requestPermissions(new String[]{READ_CONTACTS}, requestTag);
//                        }
//                    });
//        } else {
//            requestPermissions(new String[]{READ_CONTACTS}, requestTag);
//        }
//        return false;
//    }
}

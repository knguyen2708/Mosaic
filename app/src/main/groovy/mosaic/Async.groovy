package com.example.knguyen.mosaic

import android.os.AsyncTask

/**
 * Abstract AsyncTask to allow the use of Groovy closures.
 * This implementation uses `executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)` for parallelism.
 *
 * Based on Fluent, see: https://gist.github.com/melix/355185ffbc1332952cc8
 * But I think my code is more compact, probably because of influent from Swift/iOS. Not sure if it's buggy.
 *
 * Usage:
 *
 * Async.work {
 *     // Do work here, and return result of type, say, Result
 *     // Corresponds to `doInBackground` of AsyncTask
 * }.then { Result result ->
 *     // Do stuffs that you would do on main thread
 *     // Corresponds to `onPostExecute` of AsyncTask
 * }
 *
 */
class Async<Result, Progress> {

    public static Builder<Result, Progress> work(Closure<Result> work) {
        return new Builder<Result, Progress>(work)
    }

    public static class Builder<Result, Progress> {
        private Closure<Result> mWork
        private Closure<Progress> mProgress
        private Closure mThen

        private Builder(Closure<Result> work) {
            mWork = work
        }

        public Builder<Result, Progress> progress(Closure<Progress> progress) {
            mProgress = progress
        }

        public void then(Closure then) {
            mThen = then

            AsyncTask<Object, Progress, Result> task = new AsyncTask<Object, Progress, Result>() {
                @Override
                protected Result doInBackground(Object... params) {
                    return mWork.call()
                }

                @Override
                protected void onProgressUpdate(Progress... values) {
                    super.onProgressUpdate(values)

                    if (mProgress) {
                        mProgress.call(values)
                    }
                }

                @Override
                protected void onPostExecute(Result result) {
                    super.onPostExecute(result)

                    mThen(result)
                }
            }

            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }
}
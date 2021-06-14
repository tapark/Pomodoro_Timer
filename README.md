# 뽀모도로 타이머

### SeekBar
~~~kotlin
// in activity_main.xml : SeekBar 생성
<SeekBar
	android:id="@+id/seekBar"
	android:layout_width="0dp"
	android:layout_height="wrap_content"
	android:max="60" // 최대값
	android:splitTrack="false" // thumb와 progress 영영구분 X
	android:tickMark="@drawable/tick_mark" // 한틱(1분) 마크
	android:progressDrawable="@color/tick" // 진행막대 색
	android:thumb="@drawable/ic_thumb"/>// 위치조절 드래그 버튼

// in MainActivity.kt
seekBar.setOnSeekBarChangeListener(
	object:  SeekBar.OnSeekBarChangeListener {
		override fun onProgressChanged( //seekbar의 변화를 감지
			seekBar: SeekBar?,
			progress: Int, // 0~60(max) 까지 진행 정도
			fromUser: Boolean // user에 의한 조작인지?
		) {
			if (fromUser) {
				// progress(0 ~ 60)을 ms 단위로 변환 TextView에 시간으로 대입
				updateTime(progress * 60 * 1000L)
			}
		}
		// 최초 드래그 시작시 실행 (기존에 진행중이던 Timer Cancel)
		override fun onStartTrackingTouch(seekBar: SeekBar?) 		currentCountDownTimer?.cancel()
			currentCountDownTimer = null
		}
		// 드래그를 멈출때 발생
		override fun onStopTrackingTouch(seekBar: SeekBar?) {
			if (seekBar?.progress == 0 || seekBar == null)
			{
				currentCountDownTimer?.cancel()
				currentCountDownTimer = null
				soundPool.autoPause()
				return
			}
			currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)
			currentCountDownTimer?.start()

			soundPool.play(incomingId!!, 1F, 1F, 0, -1, 1F)
		}
	}
)
~~~

### CountDownTimer
~~~kotlin
// progress의 시간(initMillis) 만큼 CountDownTimer 시작 interval : 1000ms
object: CountDownTimer(initMillis, 1000L) {
	override fun onTick(millisUntilFinished: Long) {
		updateTime(millisUntilFinished)
		updateSeekBar(millisUntilFinished)
	}
	// 0에 도달했을때
	override fun onFinish() {

		updateTime(0)
		updateSeekBar(0)

		soundPool.autoPause()
		soundPool.play(clockId!!, 1F, 1F, 0, 0, 1F)
	}
}
// 취소 : CountDownTimer.cancel()
// 시작 : CountDownTimer.start()
~~~

### SoundPool
 - 리소스에 저장된 Sound 파일을 재생 (6~7초)
 - 안드로이드 Life_Cycle에 따라 재생, 일시정지, 재개, 메모리 해제 등의 작업이 필요함
~~~kotlin
//soundPool Build
private val soundPool = SoundPool.Builder().build()
// 리소스에 저장되 Sound 파일을 load
private var incomingId: Int? = null
private var clockId: Int? = null
plingId = soundPool.load(this, R.raw.pling_sound, 1)
clockId = soundPool.load(this, R.raw.clock_sound, 1)
// 재생, 일시정지(All or ID), 정지해제(All or ID), 메모리해제
soundPool.play(soundID, LeftVol, RightVol, 우선순위, 반복횟수, 재생속도)
// soundID : 재생할 파일의 resID
// leftVol : 왼쪽 볼륨 크기 (range : 0.0 ~ 1.0)
// rightVol : 오른쪽 볼륨 크기 (range : 0.0 ~ 1.0)
// priority : 우선순위 ( 0이 가장 낮음을 나타냅니다)
// loop : 반복횟수 (0일경우 1번만 재생 -1일 경우에는 무한반복)
// rate : 재생속도 (range : 0.5 ~ 2.0) 
soundPool.autoPause(), soundPool.Pause(soundID)
soundPool.autoResume(), soundPool.Resume(soundID)
soundPool.release() // 메모리 할당 해제
~~~
package com.codelab.basics

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codelab.basics.ui.theme.BasicsCodelabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { //compose로 전환
            BasicsCodelabTheme {
                // A surface container using the 'background' color from the theme
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }
    // by 키워드를 델리게이트를 사용한다. 이런식으로 사용하면 greeting에서처럼 .value를 사용하지 않는다. 코드가 더 간단해진다.
    // rememberSaveable은 내부의 값을 기억할 뿐만 아니라 구성이 변경되어도 상태를 그대로 유지한다.
    if (shouldShowOnboarding) {
        OnboardingScreen(onContinueClicked = { shouldShowOnboarding = false })
        // 가장 직관적이고 편리한 방법은 OnboardingScreen 컴포저블로 상태를 전달하는 것이다.
        // 컴포저블에 상태를 넣고 그 안에서 변경하는 건 깔끔하지 못하다.
        // 대신 버튼과 똑같은 방법(콜백 입력)으로 처리할 수 있다.
    } else {
        Greetings()
    }
    // 무언가를 숨기는 대신 특정 컴포저블을 보여줄지 말지 선택하도록 한다.
    // shouldShowOnboarding 값이 true에서 false로 변경되면 더 이상 호출되지 않는다.
    // 즉 OnboardingScreen 컴포저블이 구성에서 제거되고 greeting 컴포저블이 추가된다.
}

@Composable
fun Greetings(names: List<String> = List(1000) { "$it" }) {
    Surface(
        color = MaterialTheme.colors.background
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            LazyColumn {
//                item { Text(text = "HEADER")} 헤더도 추가할 수 있다.
                items(names) { name ->
                    Greeting(name = name)
                }
                // DSL을 사용한다. 간단히 말해 컴포저블을 직접 호출하는 대신
                // item이나 items를 사용하거나 itemsIndexed를 사용하는 것이다.
                // 그러면 LazyColumn과 연결된다. 똑같은 방법으로 LazyRow도 가능
            }
            //LazyColumn은 현재 화면 안에 있는 컴포저블만 렌더링 한다.
//            names.forEach { Greeting(name = it) }
        }
    }
}

// 상태 호이스팅: 상태를 상위 항목으로 이동시키는 과정
// 호이스트: 들어올리거나, 높이는 것을 의미한다.
// 상태 호이스팅의 이점: 1. 상태를 위로 올릴 수 있겍 하면 상태가 중복되는 버그를 막고, 컴포저블을 재사용하는데 도움이된다.
//                  2. 테스트를 용이하게 한다.
// 반면, 컴포저블의 상위 항목에서 제어하지 않는 상태는 호이스팅해서는 안된다. 아래의 Greeting과 같은 경우
// 따라서 source of truth는 해당 상태를 생성하고 제어하는 곳에 두어야한다.
@Composable
fun Greeting(name: String) {
    val expended =
        remember { mutableStateOf(false) } // 최초 값과 함께 호출할 수 있는 컴포즈 메서드로 특정 값이 변경되면 종속성을 재구성하라고 알려주는 역할을 한다. 이제 bool이 아니라 상태 인스턴스를 반환한다.
    // remember는 컴포저블이 재구성될 때마다 재설정되지 않도록 한다.
    // Greeting을 처음 호출하는 시점에 mutableStateOf이 생성된.
    // 하지만 Greeting이 재구성되면 다시 생성되지 않는다. -> false로 초기화되지 않고 재구성되기 전의 값을 기억한다.
    val extraPadding by animateDpAsState(
        targetValue = if (expended.value) 48.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 2000
        )
    )
    // 계산이 복잡해질 경우 CPU 사이클을 더 많이 사용하기 때문에 remember블록에 넣는게 나을 수도 있다.
    Surface(
        color = MaterialTheme.colors.primary,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(modifier = Modifier.padding(24.dp)) {
            Column(
                modifier = Modifier
                    .weight(1f) //Views 시스템에서 상위 요소에게 하위 요소가 변동된다는 걸 알려주는 역할. 사용 가능한 공간은 모두 차지하지만 다른 하위요소와는 겹치지 않음
                    .padding(bottom = extraPadding)
            ) {
                Text(text = "Hello,")
                Text(text = name)
            }
            OutlinedButton(onClick = {
                expended.value = !expended.value
            }) { //Button은 기본 색상을 사용/OutlinedButton은 머테리얼에 반대되는 컨셉
                Text(if (expended.value) "Show less" else "Show more")
            }
        }
    }
}

@Composable
fun OnboardingScreen(
    onContinueClicked: () -> Unit, //콜백을 전하는 것. 이 컴포저블 내에서 호출되기 때문
) {

    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to the Basics Codelab!")
            Button(
                modifier = Modifier
                    .padding(vertical = 24.dp),
                onClick = onContinueClicked
                // 이 콜백은 버튼이 클릭되면 { shouldShowOnboarding = false } 이 함수를 호출하고
                // 내부의 명령을 실행하라고 버튼에게 지시한다.
            ) {
                Text("Continue")
            }
        }
    }


}

@Preview(showBackground = true, widthDp = 320, heightDp = 320, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
    BasicsCodelabTheme {
        OnboardingScreen(onContinueClicked = {}) // Do nothing on click.
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun DefaultPreview() {
    BasicsCodelabTheme {
        MyApp()
    }
}
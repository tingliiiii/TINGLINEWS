// const ip = '127.0.0.1';
// const ip = 'localhost';
const ip = '172.20.10.5';

const fetchData = async (url, method, body) => {
	try {
		const response = await fetch(url, {
			method: method,
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(body)
		});
		return await response.json();
	} catch (e) {
		console.error('請求錯誤：', e);
		throw e;
	}
}

const startCountdown = (button, duration) => {
	let time = duration;
	button.prop('disabled', true);
	button.text(`再次傳送 (${time})`);

	const countdown = setInterval(() => {
		time--;
		button.text(`再次傳送 (${time})`);

		if (time <= 0) {
			clearInterval(countdown);
			button.prop('disabled', false);
			button.text('傳送驗證碼');
		}
	}, 1000);
}

const checkEmail = async (event) => {
	event.preventDefault();
	const email = $('#userEmail').val();

	if (!email) {
		Swal.fire('請輸入電子信箱', '', 'error');
		return;
	}

	try {
		const { state, message, data } = await fetchData(`http://${ip}:8080/tinglinews/users/email`,
			'POST', { email });
		console.log(state, message, data);
		if (!state) {
			Swal.fire('該電子信箱尚未註冊', '', 'error');
			return;
		}
		// 顯示模態框
		const modalInstance = new bootstrap.Modal($('#modal'));
		modalInstance.show();
		sendEmail(email);

	} catch (e) {
		console.error('傳送驗證碼錯誤：', e);
		Swal.fire('傳送驗證碼錯誤 請稍後再試', e, 'error');
	}
}

const sendEmail = async (email) => {

	try {
		const { state, message, data } = await fetchData(`http://${ip}:8080/tinglinews/users/otp`,
			'POST', { email });
		console.log(state, message, data);
		if(!state){
			Swal.fire('發送驗證碼失敗 請稍後再試', '', 'error');
			return;
		}
		modalInstance.hide();
		Swal.fire(message, '', 'success');
		$('#userEmail').prop('disabled', true);

		// 開始倒數計時
		const sendEmailButton = $('#send-email');
		startCountdown(sendEmailButton, 30);

	} catch (e) {
		modalInstance.hide();
		console.error('傳送驗證碼錯誤：', e);
		Swal.fire('傳送驗證碼錯誤 請稍後再試', e, 'error');
	}
}

const verifyOTP = async (event) => {
	event.preventDefault();
	const email = $('#userEmail').val();
	const otp = $('#otp').val();

	if (!email || !otp) {
		Swal.fire('未輸入電子信箱或驗證碼', '', 'error');
		return;
	}

	try {
		const { state, message, data } = await fetchData(`http://${ip}:8080/tinglinews/users/otp/verify`, 'POST',
			{ email, otp });
		console.log(state, message, data);
		if (state !== true) {
			Swal.fire(message, '', 'error');
			return;
		}
		Swal.fire(message, '', 'success');
		setTimeout(() => {
			sessionStorage.setItem('userEmail', email);
			window.location.replace('/tinglinews/user/reset.html');
		}, 1000);

	} catch (e) {
		console.error('驗證錯誤：', e);
		Swal.fire('驗證錯誤 請稍後再試', e, 'error');
	}
}

$(document).ready(() => {

	$('.header-container').load('../nav.html');
	$('.footer-container').load('../footer.html');

	const email = sessionStorage.getItem('userEmail');
	$('#userEmail').val(email);

	$('#send-email').on('click', checkEmail);
	$('#verify').on('click', verifyOTP);

	// 輸入 email 時按下 enter 鍵，觸發傳送驗證碼按鈕
	$('#userEmail').on('keypress', (event) => {
		if (event.which === 13) {
			event.preventDefault();
			$('#send-email').click();
		}
	});

	// 輸入 otp 時按下 enter 鍵，觸發驗證按鈕
	$('#otp').on('keypress', (event) => {
		if (event.which === 13) {
			event.preventDefault();
			$('#verify').click();
		}
	});

});

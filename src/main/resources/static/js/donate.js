// const ip = '127.0.0.1';
// const ip = 'localhost';
const ip = '172.20.10.5';

// 表單提交事件處理
const handleFormSubmit = async (event) => {

	event.preventDefault();

	const captcha = $('#code').val();
	if (!captcha) {
		Swal.fire('請輸入驗證碼', '', 'error');
		return;
	}
	const isCaptchaValid = await verifyCaptcha(captcha);
	if (!isCaptchaValid) {
		Swal.fire('驗證碼錯誤', '請重新輸入', 'error');
		loadCaptcha();
		return;
	}

	const data = JSON.parse(sessionStorage.getItem('userData'));

	if (!data) {
		Swal.fire('請先登入 謝謝', '', 'warning');
		setTimeout(() => {
			window.location.replace('/tinglinews/user/login.html');
		}, 1000);
		return;
	}

	const formData = {
		userEmail: $('#userEmail').val(),
		frequency: $('#frequency').val(),
		amount: $('#amount').val(),
		donateStatus: ($('#frequency').val() === '單筆') ? '已完成' : '進行中',
		userId: data.userId
	};

	sessionStorage.setItem('userEmail', formData.userEmail);
	await donate(formData);
};

const donate = async (formData) => {

	try {
		const response = await fetch(`http://${ip}:8080/tinglinews/users/donations`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(formData)
		});

		const { state, message, data } = await response.json();

		if (state) {
			Swal.fire(message, '', 'success');
			setTimeout(() => {
				window.location.replace('/tinglinews/user/profile.html');
			}, 1000);
		} else {
			Swal.fire(message, '', 'warning');
		}

	} catch (e) {
		console.error('捐款請求錯誤：', e);
		Swal.fire('捐款請求錯誤', '請稍後再試', 'error');
	}
}

const loadCaptcha = async () => {

	const captcha = $('#captcha');

	try {
		const response = await fetch(`http://${ip}:8080/tinglinews/users/captcha`);
		const { state, message, data } = await response.json();

		if (state) {
			captcha.attr('src', `data:image/jpeg;base64,${data}`);
		} else {
			Swal.fire(message, '取得 captcha 失敗', 'error');
		}

	} catch (e) {
		console.error('取得 captcha 發生錯誤：', e);
		Swal.fire('取得 captcha 發生錯誤', '請稍後再試', 'error');
	}
};

const verifyCaptcha = async (captcha) => {
	try {
		const response = await fetch(`http://${ip}:8080/tinglinews/users/captcha/verify`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({ captcha })
		});
		const { state, message, data } = await response.json();
		// console.log(state, message, data);
		return state;
	} catch (e) {
		console.error('驗證錯誤：', e);
		return false;
	}
}

$(document).ready(() => {

	const data = JSON.parse(sessionStorage.getItem('userData'));
	if (data) {
		$('.header-container').load('../nav-login.html');
	} else {
		$('.header-container').load('../nav.html');
	}
	$('.footer-container').load('../footer.html');

	if (sessionStorage.getItem('userEmail') || (data && data.userEmail)) {
		const email = sessionStorage.getItem('userEmail') || data.userEmail;
		$('#userEmail').val(email);
	}

	loadCaptcha();

	$('#donate-form').on('submit', handleFormSubmit);
});


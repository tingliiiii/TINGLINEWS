// const ip = '127.0.0.1';
const ip = 'localhost';

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

    const formData = {
        userEmail: $('#userEmail').val(),
        frequency: $('#frequency').val(),
        amount: $('#amount').val(),
        donateStatus: ($('#frequency').val() === '單筆') ? '已完成' : '進行中',
        userId: data.userId
    };
    await donate(formData);
};

const donate = async (formData) => {

    const data = JSON.parse(sessionStorage.getItem('userData'));    
    // const userId = sessionStorage.getItem('userId');

    if (!data) {
        sessionStorage.setItem('userEmail', formData.userEmail);
        Swal.fire('請先登入 謝謝', '', 'warning');
        setTimeout(() => {
            window.location.replace('/tinglinews/user/login.html');
        }, 1000);
        return;
    }

    try {
        const response = await fetch(`http://${ip}:8080/tinglinews/user/donate`, {
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
    }
}

const loadCaptcha = async () => {
	
	const captcha = $('#captcha');

    try {
        const response = await fetch(`http://${ip}:8080/tinglinews/user/captcha`);
        const { state, message, data } = await response.json();

        if (state) {
            captcha.attr('src', `data:image/jpeg;base64,${data}`);
        } else {
            Swal.fire(message, '取得 captcha 失敗', 'error');
        }

    } catch (e) {
        console.error('取得 captcha 發生錯誤：', e);
    }
};

const verifyCaptcha = async (captcha) => {
    try {
        const response = await fetch(`http://${ip}:8080/tinglinews/user/captcha`, {
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

    const userId = sessionStorage.getItem('userId');
    if (userId) {
        $('.header-container').load('../nav-login.html');
    } else {
        $('.header-container').load('../nav.html');
    }
    $('.footer-container').load('../footer.html');

    const email = sessionStorage.getItem('userEmail');
    $('#userEmail').val(email);

    loadCaptcha();

    $('#donate-form').on('submit', handleFormSubmit);
});


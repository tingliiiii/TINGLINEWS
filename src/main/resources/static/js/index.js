

// // 定義一個非同步函數來加載 HTML 內容
// const loadHTML = async (uri, containerId) => {
// 	const url = 'http://localhost:8080/tinglinews' + uri;
// 	try {
// 		const response = await fetch(url); // 等待 fetch 請求完成
// 		const data = await response.text(); // 等待回應本文內容
// 		$(containerId).html(data); // 將所得到的本文內容加入到指定容器中；使用 jQuery 設置 innerHTML
// 	} catch (e) {
// 		console.error(e);
// 	}
// };

// const clickSignupBtn = () => {
// 	$('.signup').on('click', async () => {
// 		// console.log('點擊註冊按鈕');
// 		$('.main-content').load('user/register.html', clickLoginBtn);
// 	});
// };

// const clickLoginBtn = () => {
// 	$('.login').on('click', async () => {
// 		// console.log('點擊登入按鈕');
// 		$('.main-content').load('user/login.html', clickSignupBtn);
// 	});
// }
// const ip = '127.0.0.1';
// const ip = 'localhost';
const ip = '172.20.10.5';

// 從後端抓資料
const fetchData = async () => {
	const url = `http://${ip}:8080/tinglinews/news/list/`;
	try {
		const response = await fetch(url);
		const { state, message, data } = await response.json();
		// console.log(state, message, data);

		// 小卡
		data.map((item) => {
			// Base64 字串轉圖片
			if (item.image) {
				// 檢查圖片格式並動態設置
				let imageFormat = 'jpeg'; // 默認為 jpeg
				if (item.image.startsWith('/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAoHBwkHBgoICAgLCg8LDhgQDg0NDh0VFhEYITIjJh0pKycyMTI0GyUoKDcwJzgsLCkqLjYxNTU1HyY3Pi0zP')) {
					imageFormat = 'png';
				}
				item.image = 'data:image/' + imageFormat + ';base64,' + item.image;
			}
			const contentContainer = $('<p>').html(item.content);
			const truncatedContent = contentContainer.text().substring(0, 80);
			item.content = contentContainer.text().length > 80 ? truncatedContent + '...' : truncatedContent;
		})
		renderCardData(data);

		// 大圖輪播
		data.map((item) => {
			// const contentContainer = $('<p>').html(item.content);
			// const truncatedContent = contentContainer.text().substring(0, 20);
			// item.content = contentContainer.text().length > 20 ? truncatedContent + '...' : truncatedContent;
		})
		renderCarouselData(data);

	} catch (e) {
		console.error(e);
	}
};

// 渲染小卡
const renderCardData = (data, currentPage = 1, itemsPerPage = 6) => {

	const totalItems = data.length;
	const totalPages = Math.ceil(totalItems / itemsPerPage);

	const paginate = (array, page_number) => array.slice((page_number - 1) * itemsPerPage, page_number * itemsPerPage);

	const newsItem = (item) => `
		<div class="col-12 col-sm-6 col-lg-4">
			<div class="card">
				<a href="/tinglinews/news.html?id=${item.newsId}">
					<img src="${item.image}" class="card-img" alt="${item.title}">
					<div class="card-body">
						<h5 class="card-title">${item.title}</h5>
						<p class="card-text">${item.content}</p>
					</div>
				</a>
			</div>
		</div>
	`;

	const paginatedData = paginate(data, currentPage);
	$('.row').html(paginatedData.map(newsItem).join(''));
	//	$('.row').html(data.map(newsItem).join(''));

	// Render pagination controls
	let paginationHtml = '';

	for (let i = 1; i <= totalPages; i++) {
		paginationHtml += `
			<li class="page-item ${i === currentPage ? 'active' : ''}">
                <a class="page-link" href="#" data-page="${i}">${i}</a>
            </li>
		`;
	}

	$('#pagination').html(paginationHtml);

	// Add event listeners to pagination buttons
	$('.page-link').on('click', function (event) {
		event.preventDefault();
		const page = $(this).data('page');
		renderCardData(data, page, itemsPerPage);
	});


};

// 渲染大圖輪播
const renderCarouselData = (data) => {

	const newsItem = (item) => `
		<a href = "/tinglinews/news.html?id=${item.newsId}" >
			<div class="image-ratio">
				<img src="${item.image}"
					class="d-block w-100" alt="${item.title}">
					<div class="carousel-caption">
						<h5>${item.title}</h5>
						<p>${item.publicTime}</p>
					</div>
			</div>
		</a>
	`;
	$('#carousel1').html(newsItem(data[0]));
	$('#carousel2').html(newsItem(data[1]));
	$('#carousel3').html(newsItem(data[2]));


};


// 待 DOM 加載完成之後再執行
$(document).ready(() => {

	// const currentPath = window.location.pathname;
	// console.log(currentPath);
	if (sessionStorage.getItem('userData')) {
		$('.header-container').load('nav-login.html', () => {
			// console.log('nav.html 加載完畢');
			// console.log($('#welcome').text());
		});
	} else {
		$('.header-container').load('nav.html');
	}
	// $('.main-content').load('domain.html');
	$('.footer-container').load('footer.html');

	fetchData();
});

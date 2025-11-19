<template>
    <div class="center-text no-select no-drag">
        <img src="@/assets/slogan.png" width="500" height="500" draggable="false" />
        <img src="@/assets/logo.png" width="450" height="450" draggable="false" />
    </div>

    <div class="text-center no-select no-drag">
        <br><br><br>
        <h1>五大熱門語言</h1>
        <br>

        <div class="lang-images">
            <div class="card" style="width: 18rem;" v-for="lang in langs" :key="lang.title">
                <RouterLink to="/">
                    <img :src="lang.img" class="card-img-top no-drag" alt="course" draggable="false" />
                </RouterLink>
                <div class="card-body">
                    <h5 class="card-title">{{ lang.title }}</h5>
                </div>
            </div>
        </div>

        <br><br><br>

        <div class="container">
            <div class="row">
                <div class="col-2"></div>
                <div class="col">
                    <h1>強檔新課</h1>
                </div>
                <div class="col-2"><button type="button" class="btn btn-primary">所有課程</button></div>
            </div>
        </div>

        <br>
    </div>

    <div class="lang-images no-select no-drag">
        <Carousel />
    </div>

    <br><br><br>

    <div class="text-center no-select no-drag">
        <div class="container">
            <div class="row">
                <div class="col-2"></div>
                <div class="col">
                    <h1>邂逅頂尖名師</h1>
                </div>
                <div class="col-2"><button type="button" class="btn btn-primary">老師總覽</button></div>
            </div>
        </div>

        <br>

        <div class="lang-images">
            <div class="card" style="width: 18rem;" v-for="t in teachers" :key="t.name">
                <RouterLink to="/">
                    <img :src="t.img" class="card-img-top no-drag" alt="course" draggable="false" />
                </RouterLink>
                <div class="card-body">
                    <h5 class="card-title">{{ t.name }}</h5>
                    <p>{{ t.desc }}</p>
                </div>
            </div>
        </div>
    </div>

    <footer class="bg-dark text-white mt-5 no-select no-drag">
        <div class="container py-5">
            <div class="row">
                <div class="col-md-4 mb-3">
                    <h5>末然學舍</h5>
                    <ul class="list-unstyled">
                        <li>
                            <RouterLink to="/" class="text-white text-decoration-none">品牌故事</RouterLink>
                        </li>
                        <li>
                            <RouterLink to="/" class="text-white text-decoration-none">主題報導</RouterLink>
                        </li>
                        <li>
                            <RouterLink to="/" class="text-white text-decoration-none">網站地圖</RouterLink>
                        </li>
                    </ul>
                </div>

                <div class="col-md-4 mb-3">
                    <h5>幫助</h5>
                    <ul class="list-unstyled">
                        <li>
                            <RouterLink to="/" class="text-white text-decoration-none">常見問題</RouterLink>
                        </li>
                        <li>
                            <RouterLink to="/" class="text-white text-decoration-none">客服信箱</RouterLink>
                        </li>
                        <li>
                            <RouterLink to="/" class="text-white text-decoration-none">校園方案</RouterLink>
                        </li>
                        <li>
                            <RouterLink to="/" class="text-white text-decoration-none">企業內訓</RouterLink>
                        </li>
                        <li>
                            <RouterLink to="/" class="text-white text-decoration-none">開課教學</RouterLink>
                        </li>
                        <li>
                            <RouterLink to="/teacherapply" class="text-white text-decoration-none">申請成為講師</RouterLink>
                        </li>
                    </ul>
                </div>

                <div class="col-md-4 mb-3">
                    <h5>條款與政策</h5>
                    <ul class="list-unstyled">
                        <li>
                            <RouterLink to="/" class="text-white text-decoration-none">折價券使用規則</RouterLink>
                        </li>
                        <li>
                            <RouterLink to="/" class="text-white text-decoration-none">會員規範及申訴管道</RouterLink>
                        </li>
                        <li>
                            <RouterLink to="/" class="text-white text-decoration-none">隱私權政策</RouterLink>
                        </li>
                    </ul>
                </div>

                <div class="col-md-4 mb-3">
                    <h5>下載APP</h5>
                    <ul class="list-unstyled">
                        <li><img src="@/assets/qrcode.png" width="70" height="70" draggable="false" /></li>
                    </ul>
                </div>

                <div class="col-md-4 mb-3">
                    <h5>追蹤我們</h5>
                    <RouterLink to="/" class="text-white text-decoration-none">
                        <img src="@/assets/fb.png" width="70" height="70" draggable="false" />
                    </RouterLink>
                    <RouterLink to="/" class="text-white text-decoration-none">
                        <img src="@/assets/ig.png" width="70" height="70" draggable="false" />
                    </RouterLink>
                    <RouterLink to="/" class="text-white text-decoration-none">
                        <img src="@/assets/yt.png" width="70" height="70" draggable="false" />
                    </RouterLink>
                </div>
            </div>

            <hr class="border-secondary" />
            <div class="text-center">
                <p class="mb-0">© 2025 未然學舍 | All Rights Reserved.</p>
            </div>
        </div>
    </footer>
</template>

<script setup>
import Carousel from '@/components/Carousel.vue'

const langs = [
    { title: "Java", img: new URL('@/assets/java.png', import.meta.url).href },
    { title: "Python", img: new URL('@/assets/python.png', import.meta.url).href },
    { title: "C#", img: new URL('@/assets/c.png', import.meta.url).href },
    { title: "JavaScript", img: new URL('@/assets/js.png', import.meta.url).href },
    { title: "HTML & CSS", img: new URL('@/assets/html&css.png', import.meta.url).href }
]

const teachers = [
    { name: "黃浩", desc: "Java 零基礎啟蒙教練", img: new URL('@/assets/teacher1.png', import.meta.url).href },
    { name: "曾欣儀", desc: ".NET 元件化架構大師", img: new URL('@/assets/teacher2.png', import.meta.url).href },
    { name: "鄭翔", desc: "Python 數據實戰講師", img: new URL('@/assets/teacher3.png', import.meta.url).href }
]
</script>

<style>
.center-text {
    text-align: center;
}

.lang-images {
    display: flex;
    justify-content: center;
    gap: 50px;
    flex-wrap: wrap;
}

/* ✅ 禁止拖曳與選取 */
.no-drag img,
img[draggable="false"] {
    -webkit-user-drag: none;
    user-select: none;
    pointer-events: auto;
}

.no-select {
    user-select: none;
    -webkit-user-select: none;
    -moz-user-select: none;
}
</style>

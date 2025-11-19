<template>
    <div id="carouselExampleIndicators" class="carousel slide" data-bs-ride="carousel">
        <div class="carousel-indicators">
            <button type="button" data-bs-target="#carouselExampleIndicators" data-bs-slide-to="0" class="active" aria-current="true" aria-label="Slide 1"></button>
            <button type="button" data-bs-target="#carouselExampleIndicators" data-bs-slide-to="1" aria-label="Slide 2"></button>
            <button type="button" data-bs-target="#carouselExampleIndicators" data-bs-slide-to="2" aria-label="Slide 3"></button>
        </div>
        <div class="carousel-inner">
            <div class="carousel-item active">
                <RouterLink to="/">
                    <img src="@/assets/Course1.png" class="d-block w-100" alt="廣告">
                </RouterLink>
            </div>
            <div class="carousel-item">
                <RouterLink to="/">
                    <img src="@/assets/Course2.png" class="d-block w-100" alt="廣告">
                </RouterLink>
            </div>
            <div class="carousel-item">
                <RouterLink to="/">
                    <img src="@/assets/Course3.png" class="d-block w-100" alt="廣告">
                </RouterLink>
            </div>
        </div>
        <button class="carousel-control-prev" type="button" data-bs-target="#carouselExampleIndicators" data-bs-slide="prev">
            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
            <span class="visually-hidden">Previous</span>
        </button>
        <button class="carousel-control-next" type="button" data-bs-target="#carouselExampleIndicators" data-bs-slide="next">
            <span class="carousel-control-next-icon" aria-hidden="true"></span>
            <span class="visually-hidden">Next</span>
        </button>
    </div>
</template>
    
<script setup>
import { RouterLink } from 'vue-router';

    
</script>
    
<style scoped>
    #carouselExampleIndicators {
        width: 450px;         /* 想要多寬就改，例如 70%、900px 等 */
        
        margin-right: 0;
    }
    .carousel-control-prev,
    .carousel-control-next {
        width: 5%; /* 控制箭頭點擊範圍寬度 */
    }
    .carousel-item img {
        height: 450px;        /* 想要多高自己改 */
        object-fit: cover;    /* 維持比例，必要時裁邊 */
    }
</style>
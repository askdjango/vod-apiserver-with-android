from django.conf.urls import url
from . import views

urlpatterns = [
    url(r'^rest-auth/kakao/$', views.KakaoLogin.as_view(), name='kakao-login'),
]


from django.conf.urls import include, url
from rest_framework.routers import DefaultRouter
from .views import PostViewSet


router = DefaultRouter()
router.register('post', PostViewSet)


urlpatterns = [
    url(r'^api/', include(router.urls)),
]


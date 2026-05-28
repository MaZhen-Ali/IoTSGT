import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Manage',
    component: () => import('../views/Manage.vue'),
    redirect: "/home",
    children: [  //子路由
      {
        path: 'home',
        name: 'Home',
        component: () => import('../views/Home.vue'),

      },
      {
        path: 'user',
        name: 'User',
        component: () => import('../views/User.vue'),

      }

    ]
  },
  {
    path: '/about',
    name: 'about',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
  },
  {
    path: '/Login',
    name: 'Login',
    component: () => import('../views/Login.vue'),

  },
  {
    path: '/TaskIntent',
    name: 'TasIntent',
    component: () => import('../views/TaskIntent.vue'),

  },
  {
    path: '/DeviceSelection',
    name: 'DeviceSelection',
    component: () => import('../views/DeviceSelection.vue'),

  },
  {
    path: '/SharedPhenomenon',
    name: 'SharedPhenomenon',
    component: () => import('../views/SharedPhenomenon.vue'),


  },
  {
    path: '/SystemRequirement',
    name: 'SystemRequirement',
    component: () => import('../views/SystemRequirement.vue'),


  },
  {
    path: '/GenerateDocu',
    name: 'GenerateDocu',
    component: () => import('../views/GenerateDocu.vue'),

  },

  {
    path: '/ContextDiagram',
    name: 'ContextDiagram',
    component: () => import('../views/ContextDiagram.vue'),

  },
  {
    path: '/ProblemDiagram',
    name: 'ProblemDiagram',
    component: () => import('../views/ProblemDiagram.vue'),

  },

  {
    path: '/FuncRecommendation',
    name: 'FuncRecommendation',
    component: () => import('../views/FuncRecommendation.vue'),

  },
  {
    path: '/Model',
    name: 'Model',
    component: () => import('../views/Model.vue'),

  },


  {
    path: '/Test',
    name: 'Test',
    component: () => import('../views/Test.vue'),

  },
  {
    path: '/Test1',
    name: 'Test1',
    component: () => import('../views/Test1.vue'),

  },

  {
    path: '/NoLibDeviceSelection',
    name: 'NoLibDeviceSelection',
    component: () => import('../views/NoLibDeviceSelection.vue'),

  },
  {
    path: '/NoLibSharedPhenomenon',
    name: 'NoLibSharedPhenomenon',
    component: () => import('../views/NoLibSharedPhenomenon.vue'),


  },

  {
    path: '/NoLibSoftwareReq',
    name: 'NoLibSoftwareReq',
    component: () => import('../views/NoLibSoftwareReq.vue'),

  },

  {
    path: '/DocumentEvaluation',
    name: 'EvaluationPage',
    component: () => import('../views/EvaluationPage.vue'),

  },
  {
    path: '/EvaluationPage2',
    name: 'EvaluationPage2',
    component: () => import('../views/EvaluationPage2.vue'),

  },




  {
    path: '/DeviceKnowledge',
    name: 'DeviceKnowledge',
    component: () => import('../views/DeviceKnowledge.vue'),

  },
  {
    path: '/ModelKnowledge',
    name: 'ModelKnowledge',
    component: () => import('../views/ModelKnowledge.vue'),

  },
  {
    path: '/TemplateKnowledge',
    name: 'TemplateKnowledge',
    component: () => import('../views/TemplateKnowledge.vue'),

  },

]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})



export default router

import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { CameraviewComponent } from './cameraview/cameraview.component';


const routes: Routes = [
  newFunction(),
  {path: 'dashboard', component: DashboardComponent},
  {path: 'cameraview', component: CameraviewComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
function newFunction() {
  return { path: '', redirectTo: '/dashboard', pathMatch: 'full' };
}

import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Consultation, OpdApiService, Patient, Visit } from './opd-api.service';

type Screen = 'overview' | 'patients' | 'appointments' | 'consultation';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App implements OnInit {
  screen: Screen = 'overview';
  patients: Patient[] = [];
  appointments: Visit[] = [];
  history: Consultation[] = [];
  search = '';
  patientSearch = '';
  busy = false;
  message = '';
  messageKind: 'success' | 'error' = 'success';
  selectedAppointmentId: number | null = null;
  historyPatientId: number | null = null;
  patientForm: { name: string; gender: Patient['gender']; age: number | null; phoneNumber: string } = {
    name: '',
    gender: 'FEMALE',
    age: null,
    phoneNumber: '',
  };
  appointmentForm = {
    patientId: null as number | null,
    doctorName: '',
    appointmentAt: this.defaultAppointmentTime(),
  };
  consultationForm = { bloodPressure: '', temperature: null as number | null, notes: '' };

  constructor(private readonly api: OpdApiService) {}

  ngOnInit(): void {
    this.loadAll();
  }

  get selectedAppointment(): Visit | undefined {
    return this.appointments.find((visit) => visit.id === Number(this.selectedAppointmentId));
  }

  get completedCount(): number {
    return this.appointments.filter((visit) => visit.status === 'COMPLETED').length;
  }

  get todayLabel(): string {
    return new Intl.DateTimeFormat('en', { weekday: 'long', month: 'long', day: 'numeric' }).format(new Date());
  }

  navigate(screen: Screen): void {
    this.screen = screen;
    this.message = '';
    if (screen !== 'patients') this.search = '';
    this.loadPatients();
    if (screen === 'appointments' || screen === 'overview') this.loadAppointments();
  }

  loadAll(): void {
    this.loadPatients();
    this.loadAppointments();
  }

  loadPatients(): void {
    this.api.getPatients(this.search).subscribe({
      next: (patients) => (this.patients = patients),
      error: (error) => this.showError(error),
    });
  }

  loadAppointments(): void {
    this.api.getTodayAppointments().subscribe({
      next: (appointments) => (this.appointments = appointments),
      error: (error) => this.showError(error),
    });
  }

  registerPatient(): void {
    if (this.patientForm.age === null) return;
    this.busy = true;
    this.api.createPatient({ ...this.patientForm, age: this.patientForm.age }).subscribe({
      next: () => {
        this.patientForm = { name: '', gender: 'FEMALE', age: null, phoneNumber: '' };
        this.busy = false;
        this.notify('Patient registered successfully.');
        this.loadPatients();
      },
      error: (error) => {
        this.busy = false;
        this.showError(error);
      },
    });
  }

  bookAppointment(): void {
    const appointmentAt = this.appointmentForm.appointmentAt.length === 16
      ? `${this.appointmentForm.appointmentAt}:00`
      : this.appointmentForm.appointmentAt;
    this.busy = true;
    this.api.createAppointment({ ...this.appointmentForm, appointmentAt }).subscribe({
      next: () => {
        this.busy = false;
        this.appointmentForm = {
          patientId: null,
          doctorName: '',
          appointmentAt: this.defaultAppointmentTime(),
        };
        this.notify('Appointment booked successfully.');
        this.loadAppointments();
      },
      error: (error) => {
        this.busy = false;
        this.showError(error);
      },
    });
  }

  openConsultation(visit: Visit): void {
    this.selectedAppointmentId = visit.id;
    this.historyPatientId = visit.patient.id;
    this.screen = 'consultation';
    this.loadHistory();
  }

  completeConsultation(): void {
    if (this.selectedAppointmentId === null) return;
    this.busy = true;
    this.api.completeConsultation(this.selectedAppointmentId, this.consultationForm).subscribe({
      next: () => {
        this.busy = false;
        this.consultationForm = { bloodPressure: '', temperature: null, notes: '' };
        this.selectedAppointmentId = null;
        this.notify('Consultation saved and marked complete.');
        this.loadAppointments();
        this.loadHistory();
      },
      error: (error) => {
        this.busy = false;
        this.showError(error);
      },
    });
  }

  selectHistoryPatient(): void {
    this.selectedAppointmentId = null;
    this.loadHistory();
  }

  loadHistory(): void {
    if (this.historyPatientId === null) {
      this.history = [];
      return;
    }
    this.api.getConsultations(this.historyPatientId).subscribe({
      next: (history) => (this.history = history),
      error: (error) => this.showError(error),
    });
  }

  formatDateTime(value: string): string {
    return new Intl.DateTimeFormat('en', { hour: 'numeric', minute: '2-digit' }).format(new Date(value));
  }

  formatDate(value: string): string {
    return new Intl.DateTimeFormat('en', { month: 'short', day: 'numeric', year: 'numeric' }).format(new Date(value));
  }

  patientInitials(name: string): string {
    return name.split(/\s+/).slice(0, 2).map((part) => part.charAt(0)).join('').toUpperCase();
  }

  private defaultAppointmentTime(): string {
    const date = new Date();
    date.setMinutes(0, 0, 0);
    date.setHours(date.getHours() + 1);
    return new Date(date.getTime() - date.getTimezoneOffset() * 60000).toISOString().slice(0, 16);
  }

  private notify(text: string): void {
    this.messageKind = 'success';
    this.message = text;
  }

  private showError(error: HttpErrorResponse): void {
    this.messageKind = 'error';
    this.message = error.error?.detail || error.error?.message || error.error?.error
      || (error.status === 0 ? 'Could not reach the API. Check that the backend and database are running.' : 'The request could not be completed.');
  }
}

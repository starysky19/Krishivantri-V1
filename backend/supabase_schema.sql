-- Supabase PostgreSQL Schema for KRASHIVANTRI Mapping & Drone Marketplace
-- Run in Supabase SQL Editor to enable farm_boundaries, operator_locations, and Realtime updates.

create extension if not exists "uuid-ossp";

create table if not exists public.farm_boundaries (
    id uuid primary key default uuid_generate_v4(),
    farmer_id text not null,
    farm_name text not null,
    polygon_coordinates jsonb not null,
    area_acres numeric(10, 2) not null,
    area_hectares numeric(10, 2) not null,
    center_lat double precision not null,
    center_lng double precision not null,
    created_at timestamptz not null default now()
);

create index if not exists idx_farm_boundaries_farmer on public.farm_boundaries(farmer_id);

create table if not exists public.operator_locations (
    id text primary key,
    name text not null,
    role_type text not null check (role_type in ('drone_operator', 'drone_mitra')),
    lat double precision not null,
    lng double precision not null,
    drone_type text not null default 'quadcopter',
    is_available boolean not null default true,
    heading_degrees real not null default 0,
    updated_at timestamptz not null default now()
);

-- Enable Row Level Security
alter table public.farm_boundaries enable row level security;
alter table public.operator_locations enable row level security;

create policy "Allow authenticated or anon farm boundary read/write"
    on public.farm_boundaries for all
    using (true)
    with check (true);

create policy "Allow realtime operator location read/write"
    on public.operator_locations for all
    using (true)
    with check (true);

-- Enable Supabase Realtime broadcasts for live Drone Operator & Drone Mitra positions
alter publication supabase_realtime add table public.operator_locations;
alter publication supabase_realtime add table public.farm_boundaries;
